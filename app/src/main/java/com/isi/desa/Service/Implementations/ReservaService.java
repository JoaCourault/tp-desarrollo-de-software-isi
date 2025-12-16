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
    public List<HabitacionDisponibilidadDTO> consultarDisponibilidad(LocalDate desde, LocalDate hasta, String tipoHabitacion) {

        List<HabitacionDisponibilidadDTO> resultado = new ArrayList<>();

        // 1. Obtener habitaciones (Proyección)
        List<HabitacionResumen> habitacionesRaw = habitacionRepository.findAllResumen();

        // 2. Obtener estadías y reservas en el rango
        List<Estadia> estadiasEnRango = estadiaRepository.findEstadiasEnRango(
                desde.atStartOfDay(),
                hasta.atTime(LocalTime.MAX)
        );

        List<Reserva> reservasEnRango = reservaRepository.findReservasEnRango(
                desde.atStartOfDay(),
                hasta.atTime(LocalTime.MAX)
        );

        LocalDate hoy = LocalDate.now();

        for (HabitacionResumen h : habitacionesRaw) {

            String tipoActualBD = (h.getTipoHabitacionStr() != null) ? h.getTipoHabitacionStr().toUpperCase() : "";

            // Filtro: Si viene un tipoHabitacion del front y no coincide con la BD, saltamos
            if (tipoHabitacion != null && !tipoHabitacion.isBlank()) {
                if (!tipoActualBD.equals(tipoHabitacion.toUpperCase())) {
                    continue; // Salta al siguiente ciclo del for
                }
            }

            HabitacionDisponibilidadDTO disponibilidadDTO = new HabitacionDisponibilidadDTO();
            HabitacionDTO habDTO = new HabitacionDTO();

            // Mapeo básico de la habitación
            habDTO.setIdHabitacion(h.getIdHabitacion());
            habDTO.setNumero(h.getNumero());
            habDTO.setPrecio(h.getPrecio());
            habDTO.setCapacidad(h.getCapacidad());

            // --- Mapeamos el Enum usando el valor real de la BD ---
            try {
                if (!tipoActualBD.isEmpty()) {
                    habDTO.setTipoHabitacion(TipoHabitacion.valueOf(tipoActualBD));
                } else {
                    habDTO.setTipoHabitacion(null);
                }
            } catch (Exception e) {
                // Si el string en BD no coincide con el ENUM, ponemos null para no romper
                habDTO.setTipoHabitacion(null);
            }

            disponibilidadDTO.setHabitacion(habDTO);

            List<DisponibilidadDiaDTO> dias = new ArrayList<>();
            LocalDate fechaActual = desde;

            // Convertimos el ID de la habitación actual a String limpio para comparar fácil
            String idHabitacionActual = String.valueOf(h.getIdHabitacion()).trim();

            while (!fechaActual.isAfter(hasta)) {

                String estadoDia = "DISPONIBLE";
                boolean ocupada = false;

                // 1. PRIORIDAD MÁXIMA: Estado Físico
                if ("MANTENIMIENTO".equals(h.getEstado())) {
                    estadoDia = "MANTENIMIENTO";
                    ocupada = true;
                }
                else if (fechaActual.equals(hoy) && "OCUPADA".equals(h.getEstado())) {
                    estadoDia = "OCUPADA";
                    ocupada = true;
                }
                else {
                    // 2. ESTADÍAS
                    for (Estadia e : estadiasEnRango) {
                        boolean esDeEstaHabitacion = e.getHabitaciones().stream()
                                .anyMatch(hab -> String.valueOf(hab.getIdHabitacion()).trim().equalsIgnoreCase(idHabitacionActual));

                        if (esDeEstaHabitacion) {
                            LocalDate checkInDate = e.getCheckIn().toLocalDate();
                            LocalDate checkOutDate = e.getCheckOut().toLocalDate();

                            // Si fechaActual es igual o mayor al checkIn Y menor al checkOut
                            if (!fechaActual.isBefore(checkInDate) && fechaActual.isBefore(checkOutDate)) {
                                estadoDia = "OCUPADA";
                                ocupada = true;
                                break;
                            }
                        }
                    }

                    // 3. RESERVAS (Solo si no está ocupada ya)
                    if (!ocupada) {
                        for (Reserva r : reservasEnRango) {

                            boolean esDeEstaHabitacion = String.valueOf(r.getHabitacion().getIdHabitacion()).trim()
                                    .equalsIgnoreCase(idHabitacionActual);

                            if (esDeEstaHabitacion) {
                                LocalDate rIngreso = r.getFechaIngreso().toLocalDate();
                                LocalDate rEgreso = r.getFechaEgreso().toLocalDate();

                                // Lógica: [Ingreso, Egreso) -> El día de ingreso está ocupado, el de egreso libre
                                if (!fechaActual.isBefore(rIngreso) && fechaActual.isBefore(rEgreso)) {
                                    ocupada = true;
                                    estadoDia = "RESERVADA";
                                    break;
                                }
                            }
                        }
                    }
                }

                dias.add(new DisponibilidadDiaDTO(fechaActual, estadoDia));
                fechaActual = fechaActual.plusDays(1);
            }

            disponibilidadDTO.setDisponibilidad(dias);
            resultado.add(disponibilidadDTO);
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
            // Validar existencia
            RuntimeException error = reservaValidator.validateEliminar(id);
            if (error != null) throw error;

            // Eliminar (Esto libera la habitación automáticamente para los algoritmos de disponibilidad)
            reservaDAO.eliminar(id);
        }
    }
}