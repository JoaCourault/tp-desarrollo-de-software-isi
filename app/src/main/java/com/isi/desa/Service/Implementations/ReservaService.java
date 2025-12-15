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
    public List<HabitacionDisponibilidadDTO> consultarDisponibilidad(LocalDate desde, LocalDate hasta) {

        List<HabitacionDisponibilidadDTO> resultado = new ArrayList<>();

        // 1. Obtener habitaciones (Proyección)
        List<HabitacionResumen> habitacionesRaw = habitacionRepository.findAllResumen();

        // 2. Obtener estadías
        List<Estadia> estadiasEnRango = estadiaRepository.findEstadiasEnRango(
                desde.atStartOfDay(),
                hasta.atTime(LocalTime.MAX)
        );

        List<Reserva> reservasEnRango = reservaRepository.findReservasEnRango(
                desde.atStartOfDay(),
                hasta.atTime(LocalTime.MAX)
        );

        LocalDate hoy = LocalDate.now(); // Guardamos la fecha de hoy para comparar

        for (HabitacionResumen h : habitacionesRaw) {

            HabitacionDisponibilidadDTO disponibilidadDTO = new HabitacionDisponibilidadDTO();
            HabitacionDTO habDTO = new HabitacionDTO();

            habDTO.setIdHabitacion(h.getIdHabitacion());
            habDTO.setNumero(h.getNumero());
            habDTO.setPrecio(h.getPrecio());
            habDTO.setCapacidad(h.getCapacidad());

            // Manejo seguro del Enum TipoHabitacion
            try {
                if (h.getDetalles() != null) {
                    habDTO.setTipoHabitacion(TipoHabitacion.valueOf(h.getDetalles().toUpperCase()));
                } else {
                    habDTO.setTipoHabitacion(null);
                }
            } catch (Exception e) {
                habDTO.setTipoHabitacion(null);
            }

            disponibilidadDTO.setHabitacion(habDTO);

            // Lógica de Disponibilidad día por día
            List<DisponibilidadDiaDTO> dias = new ArrayList<>();
            LocalDate fechaActual = desde;

            while (!fechaActual.isAfter(hasta)) {

                String estadoDia = "DISPONIBLE";
                boolean ocupada = false;

                // 1. PRIORIDAD MÁXIMA: Estado Físico (Mantenimiento o Check-in recién hecho)
                // Si la habitación dice "OCUPADA" y estamos pintando el día de HOY, es OCUPADA sí o sí.
                if ("MANTENIMIENTO".equals(h.getEstado())) {
                    estadoDia = "MANTENIMIENTO";
                    ocupada = true;
                }
                else if (fechaActual.equals(hoy) && "OCUPADA".equals(h.getEstado())) {
                    // Forzamos el rojo si hoy la habitación está ocupada físicamente
                    estadoDia = "OCUPADA";
                    ocupada = true;
                }
                else {
                    // 2. Si no es físico, buscamos en las ESTADÍAS (Historial/Futuro)
                    for (Estadia e : estadiasEnRango) {
                        // Verificamos si la estadía pertenece a esta habitación
                        boolean esDeEstaHabitacion = e.getHabitaciones().stream()
                                .anyMatch(habEntidad -> String.valueOf(habEntidad.getIdHabitacion()).equals(String.valueOf(h.getIdHabitacion())));

                        if (esDeEstaHabitacion) {
                            LocalDate checkInDate = e.getCheckIn().toLocalDate();
                            LocalDate checkOutDate = e.getCheckOut().toLocalDate();

                            // Lógica: Fecha actual está dentro del rango (inclusive in, exclusivo out)
                            if (!fechaActual.isBefore(checkInDate) && fechaActual.isBefore(checkOutDate)) {
                                estadoDia = "OCUPADA";
                                ocupada = true;
                                break;
                            }
                        }
                    }

                    // 3. Chequeo de RESERVAS (Solo si no está ocupada ya)
                    if (!ocupada) {
                        for (Reserva r : reservasEnRango) {
                            boolean esDeEstaHabitacion = String.valueOf(r.getHabitacion().getIdHabitacion())
                                    .equals(String.valueOf(h.getIdHabitacion()));

                            if (esDeEstaHabitacion) {
                                LocalDate rIngreso = r.getFechaIngreso().toLocalDate();
                                LocalDate rEgreso = r.getFechaEgreso().toLocalDate();

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