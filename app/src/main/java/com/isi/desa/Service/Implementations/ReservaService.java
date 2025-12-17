package com.isi.desa.Service.Implementations;

import com.isi.desa.Dao.Implementations.ReservaDAO;
import com.isi.desa.Dao.Repositories.EstadiaRepository;
import com.isi.desa.Dao.Repositories.HabitacionRepository;
import com.isi.desa.Dao.Repositories.ReservaRepository;
import com.isi.desa.Dto.Reserva.CrearReservaRequestDTO;
import com.isi.desa.Dto.Reserva.DisponibilidadDiaDTO;
import com.isi.desa.Dto.Reserva.HabitacionDisponibilidadDTO;
import com.isi.desa.Dto.Reserva.ReservaDetalleDTO;
import com.isi.desa.Dto.Reserva.ReservaListadoDTO;
import com.isi.desa.Model.Entities.Estadia.Estadia;
import com.isi.desa.Model.Entities.Habitacion.Habitacion;
import com.isi.desa.Model.Entities.Reserva.Reserva;
import com.isi.desa.Model.Enums.EstadoHabitacion;
import com.isi.desa.Model.Enums.EstadoReserva;
import com.isi.desa.Service.Interfaces.IReservaService;
import com.isi.desa.Service.Interfaces.Validators.IReservaValidator;
import com.isi.desa.Utils.Mappers.HabitacionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservaService implements IReservaService {

    @Autowired
    private ReservaDAO reservaDAO;
    @Autowired
    private ReservaRepository reservaRepository;
    @Autowired
    private HabitacionRepository habitacionRepository;
    @Autowired
    private EstadiaRepository estadiaRepository;

    @Autowired
    private HabitacionMapper habitacionMapper;

    @Autowired
    private IReservaValidator reservaValidator;

    @Override
    @Transactional
    public void realizarReserva(CrearReservaRequestDTO request) {

        if (request.getReservas() == null || request.getReservas().isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos una habitación.");
        }

        for (ReservaDetalleDTO item : request.getReservas()) {

            if (!item.getFechaHasta().isAfter(item.getFechaDesde())) {
                throw new IllegalArgumentException("La fecha de egreso debe ser posterior a la de ingreso (Mínimo 1 noche) en habitación " + item.getIdHabitacion());
            }

            if (item.getFechaDesde().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("No se puede reservar en una fecha anterior al día de hoy.");
            }

            // Validar existencia de habitación
            Habitacion habitacion = habitacionRepository.findById(String.valueOf((item.getIdHabitacion())))
                    .orElseThrow(() -> new RuntimeException("Habitación no encontrada: " + item.getIdHabitacion()));

            // Definir fechas con hora exacta
            LocalDateTime ingreso = item.getFechaDesde().atTime(14, 0);
            LocalDateTime egreso = item.getFechaHasta().atTime(10, 0);

            // --- VALIDACIÓN DE CONFLICTOS (Superposición) ---
            List<Reserva> conflictos = reservaRepository.findReservasConflictivas(
                    habitacion.getIdHabitacion(),
                    ingreso,
                    egreso
            );

            if (!conflictos.isEmpty()) {
                throw new IllegalArgumentException("La habitación " + habitacion.getNumero() + " ya tiene una reserva en las fechas seleccionadas.");
            }
            // ------------------------------------------------

            Reserva nuevaReserva = new Reserva();
            nuevaReserva.setEstado(EstadoReserva.RESERVADA);
            nuevaReserva.setFechaIngreso(ingreso);
            nuevaReserva.setFechaEgreso(egreso);
            nuevaReserva.setHabitacion(habitacion);
            nuevaReserva.setNombreHuesped(request.nombreCliente);
            nuevaReserva.setApellidoHuesped(request.getApellidoCliente());
            nuevaReserva.setTelefonoHuesped(request.getTelefonoCliente());

            reservaDAO.guardar(nuevaReserva);
        }
    }

    @Override
    public List<HabitacionDisponibilidadDTO> consultarDisponibilidad(LocalDateTime desde, LocalDateTime hasta, String tipoHabitacion) {
        if (hasta.isBefore(desde)) throw new IllegalArgumentException("Fecha hasta anterior a desde");
        List<HabitacionDisponibilidadDTO> resultado = new ArrayList<>();
        List<Habitacion> habitaciones;
        if (tipoHabitacion != null && !tipoHabitacion.isEmpty()) {
            habitaciones = habitacionRepository.findAll().stream().filter(h -> h.getTipoHabitacion() != null && h.getTipoHabitacion().toString().equals(tipoHabitacion)).collect(Collectors.toList());
        } else {
            habitaciones = habitacionRepository.findAll();
        }

        for (Habitacion hab : habitaciones) {
            HabitacionDisponibilidadDTO dto = new HabitacionDisponibilidadDTO();
            dto.setHabitacion(habitacionMapper.toDTO(hab));
            List<DisponibilidadDiaDTO> dias = new ArrayList<>();

            if (hab.getEstado() == EstadoHabitacion.FUERA_DE_SERVICIO) {
                LocalDateTime current = desde;
                while (!current.isAfter(hasta)) {
                    DisponibilidadDiaDTO d = new DisponibilidadDiaDTO(); d.setFecha(current.toLocalDate()); d.setEstado("MANTENIMIENTO"); dias.add(d); current = current.plusDays(1);
                }
                dto.setDisponibilidad(dias); resultado.add(dto); continue;
            }

            List<Reserva> reservasHab = reservaRepository.findReservasEnRango(desde, hasta).stream()
                    .filter(r -> r.getHabitacion().getIdHabitacion().equals(hab.getIdHabitacion())).collect(Collectors.toList());
            List<Estadia> estadiasHab = estadiaRepository.findEstadiasPorHabitacionYFecha(hab.getIdHabitacion(), desde, hasta);

            LocalDateTime current = desde;
            while (!current.isAfter(hasta)) {
                DisponibilidadDiaDTO diaDTO = new DisponibilidadDiaDTO();
                diaDTO.setFecha(current.toLocalDate());
                final LocalDate diaAnalizadoDate = current.toLocalDate();

                // Lógica de Estado Principal (Ocupada / Reservada / Disponible)
                // 1. ESTADIA (OCUPADA)
                java.util.Optional<Estadia> estadiaMatch = estadiasHab.stream().filter(e -> {
                    if (e.getCheckIn().toLocalDate().isAfter(diaAnalizadoDate)) return false;
                    if (e.getCheckOut() == null) return true;
                    return e.getCheckOut().toLocalDate().isAfter(diaAnalizadoDate);
                }).findFirst();

                if (estadiaMatch.isPresent()) {
                    diaDTO.setEstado("OCUPADA");
                } else {
                    // 2. RESERVA (RESERVADA)
                    java.util.Optional<Reserva> reservaMatch = reservasHab.stream().filter(r ->
                            (r.getEstado() == null || r.getEstado() == EstadoReserva.RESERVADA) &&
                                    !r.getFechaIngreso().toLocalDate().isAfter(diaAnalizadoDate) &&
                                    r.getFechaEgreso().toLocalDate().isAfter(diaAnalizadoDate) // Egreso > Dia
                    ).findFirst();

                    if (reservaMatch.isPresent()) {
                        diaDTO.setEstado("RESERVADA");
                        diaDTO.setIdReserva(reservaMatch.get().getIdReserva());
                    } else {
                        diaDTO.setEstado("DISPONIBLE");
                    }
                }

                // Buscamos si hay alguna reserva que TERMINE exactamente este día.
                boolean haySalidaEstadia = estadiasHab.stream().anyMatch(e ->
                        e.getCheckOut() != null && e.getCheckOut().toLocalDate().isEqual(diaAnalizadoDate)
                );

                // 2. Verificamos si es Fin de RESERVA (Sin CheckIn aún)
                boolean haySalidaReserva = reservasHab.stream().anyMatch(r ->
                        (r.getEstado() == null || r.getEstado() == EstadoReserva.RESERVADA) &&
                                r.getFechaEgreso().toLocalDate().isEqual(diaAnalizadoDate)
                );

                if (haySalidaEstadia) {
                    diaDTO.setEsSalida(true);
                    diaDTO.setTipoSalida("ESTADIA"); // Prioridad: Si hay checkout, es Checkout
                } else if (haySalidaReserva) {
                    diaDTO.setEsSalida(true);
                    diaDTO.setTipoSalida("RESERVA");
                }

                dias.add(diaDTO);
                current = current.plusDays(1);
            }
            dto.setDisponibilidad(dias);
            resultado.add(dto);
        }
        return resultado;
    }

    @Override
    public List<ReservaListadoDTO> buscarParaCancelar(String apellido, String nombre) {
        RuntimeException error = reservaValidator.validateBuscar(apellido, nombre);
        if (error != null) throw error;
        List<Reserva> reservas = reservaDAO.buscarPorHuesped(apellido, nombre);
        List<ReservaListadoDTO> dtos = new ArrayList<>();
        for (Reserva r : reservas) {
            ReservaListadoDTO dto = new ReservaListadoDTO();
            dto.idReserva = r.getIdReserva(); dto.apellidoHuesped = r.getApellidoHuesped(); dto.nombreHuesped = r.getNombreHuesped(); dto.fechaIngreso = r.getFechaIngreso(); dto.fechaEgreso = r.getFechaEgreso();
            if (r.getHabitacion() != null) { dto.numeroHabitacion = r.getHabitacion().getNumero(); dto.tipoHabitacion = r.getHabitacion().getTipoHabitacion().toString(); }
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    @Transactional
    public void cancelarReservas(List<String> idsReservas) {
        if (idsReservas == null || idsReservas.isEmpty()) return;
        for (String id : idsReservas) {
            Reserva reserva = reservaRepository.findById(id).orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
            if (reserva.getEstado() == EstadoReserva.EFECTIVIZADA || reserva.getEstado() == EstadoReserva.FINALIZADA) throw new RuntimeException("No se puede cancelar");
            reserva.setEstado(EstadoReserva.CANCELADA);
            reservaDAO.guardar(reserva);
        }
    }
}