package com.isi.desa.Service.Implementations;

import com.isi.desa.Dao.Implementations.ReservaDAO;
import com.isi.desa.Dao.Repositories.EstadiaRepository;
import com.isi.desa.Dao.Repositories.HabitacionRepository;
import com.isi.desa.Dao.Repositories.Projections.HabitacionResumen;
import com.isi.desa.Dao.Repositories.ReservaRepository;
import com.isi.desa.Dto.Habitacion.HabitacionDTO;
import com.isi.desa.Dto.Reserva.CrearReservaRequestDTO;
import com.isi.desa.Dto.Reserva.DisponibilidadDiaDTO;
import com.isi.desa.Dto.Reserva.HabitacionDisponibilidadDTO;
import com.isi.desa.Dto.Reserva.ReservaDetalleDTO;
import com.isi.desa.Model.Entities.Estadia.Estadia;
import com.isi.desa.Model.Entities.Habitacion.Habitacion;
import com.isi.desa.Model.Entities.Reserva.Reserva;
import com.isi.desa.Model.Enums.EstadoHabitacion;
import com.isi.desa.Model.Enums.TipoHabitacion;
import com.isi.desa.Service.Interfaces.IReservaService;
import com.isi.desa.Utils.Mappers.HabitacionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.isi.desa.Dto.Reserva.ReservaListadoDTO;
import com.isi.desa.Service.Interfaces.Validators.IReservaValidator;
import java.time.LocalTime;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import com.isi.desa.Model.Enums.EstadoReserva;
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
    private IReservaValidator reservaValidator; // Inyectar Validator

    @Override
    @Transactional
    public void realizarReserva(CrearReservaRequestDTO request) {

        // Validaciones básicas de entrada
        if (request.getReservas() == null || request.getReservas().isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos una habitación.");
        }

        for (ReservaDetalleDTO item : request.getReservas()) {
            // A.1 Validar coherencia de fechas (Desde < Hasta)
            if (item.getFechaDesde().isAfter(item.getFechaHasta())) {
                throw new IllegalArgumentException("Fecha ingreso mayor a egreso en habitación " + item.getIdHabitacion());
            }
            // A.2 Validar FECHA PASADA
            if (item.getFechaDesde().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("No se puede reservar en una fecha anterior al día de hoy.");
            }
            // B. Buscar habitación
            Habitacion habitacion = habitacionRepository.findById(String.valueOf((item.getIdHabitacion())))
                    .orElseThrow(() -> new RuntimeException("Habitación no encontrada: " + item.getIdHabitacion()));

            Reserva nuevaReserva = new Reserva();

            nuevaReserva.setEstado(EstadoReserva.RESERVADA);
            // Usamos getFechaDesde() para el ingreso
            // Configuramos Check-in a las 14:00
            nuevaReserva.setFechaIngreso(item.getFechaDesde().atTime(14, 0));

            // Usamos getFechaHasta() para el egreso (Check-out 10:00 AM)
            nuevaReserva.setFechaEgreso(item.getFechaHasta().atTime(10, 0));

            nuevaReserva.setHabitacion(habitacion);

            // Seteo de Datos del Huésped
            nuevaReserva.setNombreHuesped(request.getNombreCliente());
            nuevaReserva.setApellidoHuesped(request.getApellidoCliente());
            nuevaReserva.setTelefonoHuesped(request.getTelefonoCliente());

            // D. Guardar
            reservaDAO.guardar(nuevaReserva);
        }
    }

    @Override
    public List<HabitacionDisponibilidadDTO> consultarDisponibilidad(LocalDateTime desde, LocalDateTime hasta, String tipoHabitacion) {
        List<HabitacionDisponibilidadDTO> resultado = new ArrayList<>();

        // 1. Obtener Habitaciones
        List<Habitacion> habitaciones;
        if (tipoHabitacion != null && !tipoHabitacion.isEmpty()) {
            habitaciones = habitacionRepository.findAll().stream()
                    .filter(h -> h.getTipoHabitacion() != null && h.getTipoHabitacion().toString().equals(tipoHabitacion))
                    .collect(Collectors.toList());
        } else {
            habitaciones = habitacionRepository.findAll();
        }

        for (Habitacion hab : habitaciones) {
            HabitacionDisponibilidadDTO dto = new HabitacionDisponibilidadDTO();
            dto.setHabitacion(habitacionMapper.toDTO(hab));
            List<DisponibilidadDiaDTO> dias = new ArrayList<>();

            // --- ESCENARIO 1: HABITACIÓN ROTA ---
            if (hab.getEstado() == EstadoHabitacion.FUERA_DE_SERVICIO) {
                LocalDateTime current = desde;
                while (!current.isAfter(hasta.minusDays(1))) {
                    DisponibilidadDiaDTO diaDTO = new DisponibilidadDiaDTO();
                    diaDTO.setFecha(current.toLocalDate());
                    diaDTO.setEstado("MANTENIMIENTO");
                    dias.add(diaDTO);
                    current = current.plusDays(1);
                }
                dto.setDisponibilidad(dias);
                resultado.add(dto);
                continue;
            }

            // --- ESCENARIO 2: HABITACIÓN FUNCIONAL ---

            // a. Traer Reservas (Solo validas)
            List<Reserva> reservasHab = reservaRepository.findReservasEnRango(desde, hasta).stream()
                    .filter(r -> r.getHabitacion().getIdHabitacion().equals(hab.getIdHabitacion()))
                    .collect(Collectors.toList());

            // b. Traer Estadías
            List<Estadia> estadiasHab = estadiaRepository.findEstadiasPorHabitacionYFecha(hab.getIdHabitacion(), desde, hasta);

            LocalDateTime current = desde;
            while (current.isBefore(hasta)) {
                DisponibilidadDiaDTO diaDTO = new DisponibilidadDiaDTO();
                diaDTO.setFecha(current.toLocalDate());

                final LocalDateTime fechaAnalizada = current;
                LocalDate diaAnalizadoDate = fechaAnalizada.toLocalDate();

                // 1. PRIORIDAD ALTA: ESTADÍA (OCUPADA - ROJO)
                // Lógica: Si hoy duermen aquí. (CheckIn <= Hoy < CheckOut)
                // IMPORTANTE: Si el checkout es HOY, hoy ya no cuenta como ocupado (se van a las 10am)
                java.util.Optional<Estadia> estadiaMatch = estadiasHab.stream().filter(e ->
                        !e.getCheckIn().toLocalDate().isAfter(diaAnalizadoDate) && // CheckIn fue hoy o antes
                                e.getCheckOut().toLocalDate().isAfter(diaAnalizadoDate)    // CheckOut es MAÑANA o después
                ).findFirst();

                if (estadiaMatch.isPresent()) {
                    diaDTO.setEstado("OCUPADA");
                    // Opcional: Si quisieras vincular estadía, podrías agregar idEstadia al DTO
                } else {
                    // 2. PRIORIDAD MEDIA: RESERVA (RESERVADA - AMARILLO)
                    // Si el checkout es HOY, hoy queda libre para nueva reserva.
                    java.util.Optional<Reserva> reservaMatch = reservasHab.stream().filter(r ->
                            (r.getEstado() == null || r.getEstado() == EstadoReserva.RESERVADA) &&
                                    !r.getFechaIngreso().toLocalDate().isAfter(diaAnalizadoDate) && // Ingreso <= Hoy
                                    r.getFechaEgreso().toLocalDate().isAfter(diaAnalizadoDate)      // Egreso > Hoy (O sea, duermen esta noche)
                    ).findFirst();

                    if (reservaMatch.isPresent()) {
                        diaDTO.setEstado("RESERVADA");
                        // Guardamos el ID para el Check-In
                        diaDTO.setIdReserva(reservaMatch.get().getIdReserva());
                    } else {
                        // 3. PRIORIDAD BAJA: LIBRE (DISPONIBLE - VERDE)
                        diaDTO.setEstado("DISPONIBLE");
                    }
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
        // 1. Validación
        RuntimeException error = reservaValidator.validateBuscar(apellido, nombre);
        if (error != null) throw error;

        // 2. Búsqueda
        List<Reserva> reservas = reservaDAO.buscarPorHuesped(apellido, nombre);

        // 3. Mapeo a DTO (Grid)
        List<ReservaListadoDTO> dtos = new ArrayList<>();
        for (Reserva r : reservas) {
            ReservaListadoDTO dto = new ReservaListadoDTO();
            dto.idReserva = r.getIdReserva();
            dto.apellidoHuesped = r.getApellidoHuesped();
            dto.nombreHuesped = r.getNombreHuesped();
            dto.fechaIngreso = r.getFechaIngreso();
            dto.fechaEgreso = r.getFechaEgreso();

            // Accedemos a la habitación relacionada
            if (r.getHabitacion() != null) {
                dto.numeroHabitacion = r.getHabitacion().getNumero();
                dto.tipoHabitacion = r.getHabitacion().getDetalles();
            }
            dtos.add(dto);
        }

        if (dtos.isEmpty()) {
            // Opcional: lanzar error si el requerimiento dice "Mostrar error si no hay concordancia"
            throw new RuntimeException("No existen reservas para los criterios de búsqueda");
        }

        return dtos;
    }

    @Override
    @Transactional
    public void cancelarReservas(List<String> idsReservas) {
        if (idsReservas == null || idsReservas.isEmpty()) return;

        for (String id : idsReservas) {
            // Buscamos la reserva
            Reserva reserva = reservaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));

            // Validamos que no esté efectuada/finalizada
            if (reserva.getEstado() == EstadoReserva.EFECTIVIZADA || reserva.getEstado() == EstadoReserva.FINALIZADA) {
                throw new RuntimeException("No se puede cancelar una reserva que ya fue efectuada o finalizada.");
            }

            // Cambiamos el estado a CANCELADA
            reserva.setEstado(EstadoReserva.CANCELADA);
            reservaDAO.guardar(reserva);

        }
    }
}