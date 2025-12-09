package com.isi.desa.Service.Implementations;

import com.isi.desa.Dao.Implementations.ReservaDAO;
import com.isi.desa.Dao.Repositories.EstadiaRepository;
import com.isi.desa.Dao.Repositories.HabitacionRepository;
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
import com.isi.desa.Service.Interfaces.IReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ReservaService implements IReservaService {

    @Autowired
    private ReservaDAO reservaDAO;

    @Autowired
    private ReservaRepository reservaRepository; // Inyectamos el repo directo para la consulta custom

    @Autowired
    private HabitacionRepository habitacionRepository; // Necesitamos buscar la entidad Habitación

    // INYECTAR EL REPOSITORIO DE ESTADÍAS
    @Autowired
    private EstadiaRepository estadiaRepository;

    @Override
    @Transactional
    public void realizarReserva(CrearReservaRequestDTO request) {

        if (request.getReservas() == null || request.getReservas().isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos una habitación.");
        }

        // --- BUCLE ---
        for (ReservaDetalleDTO item : request.getReservas()) {

            // A.1 Validar coherencia de fechas (Desde < Hasta)
            if (item.getFechaDesde().isAfter(item.getFechaHasta())) {
                throw new IllegalArgumentException("Fecha ingreso mayor a egreso en habitación " + item.getIdHabitacion());
            }

            // A.2 Validar FECHA PASADA (NUEVO) 🕒🚫
            if (item.getFechaDesde().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("No se puede reservar en una fecha anterior al día de hoy.");
            }

            // B. Buscar habitación
            Habitacion habitacion = habitacionRepository.findById(item.getIdHabitacion())
                    .orElseThrow(() -> new RuntimeException("Habitación no encontrada: " + item.getIdHabitacion()));

            // C. Crear Reserva
            Reserva nuevaReserva = new Reserva();
            nuevaReserva.setIdReserva("RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

            nuevaReserva.setFechaIngreso(item.getFechaDesde());
            nuevaReserva.setFechaEgreso(item.getFechaHasta());
            nuevaReserva.setHabitacion(habitacion);

            nuevaReserva.setNombreHuesped(request.getNombreCliente());
            nuevaReserva.setApellidoHuesped(request.getApellidoCliente());
            nuevaReserva.setTelefonoHuesped(request.getTelefonoCliente());

            // D. Guardar
            reservaDAO.guardar(nuevaReserva);
        }
    }

    public List<HabitacionDisponibilidadDTO> consultarDisponibilidad(LocalDate desde, LocalDate hasta) {

        List<Habitacion> habitaciones = habitacionRepository.findAll();

        // 1. Buscar RESERVAS (Intención futura)
        List<Reserva> reservasEnRango = reservaRepository.findReservasEnRango(desde, hasta);

        // 2. Buscar ESTADÍAS (Ocupación real actual)
        // Convertimos LocalDate a LocalDateTime para comparar con la BD
        List<Estadia> estadiasEnRango = estadiaRepository.findEstadiasEnRango(
                desde.atStartOfDay(),
                hasta.atTime(23, 59, 59)
        );

        List<HabitacionDisponibilidadDTO> resultado = new ArrayList<>();

        for (Habitacion habitacion : habitaciones) {

            HabitacionDTO habDTO = new HabitacionDTO();
            habDTO.setIdHabitacion(habitacion.getIdHabitacion());
            habDTO.setNumero(habitacion.getNumero());
            habDTO.setPrecio((habitacion.getPrecio() != null) ? habitacion.getPrecio().floatValue() : 0f);
            habDTO.setTipoHabitacion(habitacion.getDetalles()); // O getTipoHabitacion() si lo tienes
            habDTO.setCapacidad(habitacion.getCapacidad());
            List<DisponibilidadDiaDTO> dias = new ArrayList<>();
            LocalDate fechaActual = desde;

            while (!fechaActual.isAfter(hasta)) {
                String estadoDia = "DISPONIBLE";

                // A. MANTENIMIENTO (Prioridad Máxima)
                if (habitacion.getEstado() == EstadoHabitacion.FUERA_DE_SERVICIO) { // O FUERA_DE_SERVICIO según tu enum
                    estadoDia = "MANTENIMIENTO";
                } else {

                    // B. ESTADÍA (Prioridad 1: Si hay gente adentro, está OCUPADA)
                    boolean ocupada = false;
                    for (Estadia e : estadiasEnRango) {
                        // Verificamos si esta estadía incluye a la habitación actual
                        boolean incluyeHabitacion = e.getHabitaciones().stream()
                                .anyMatch(h -> h.getIdHabitacion().equals(habitacion.getIdHabitacion()));

                        if (incluyeHabitacion) {
                            LocalDate in = e.getCheckIn().toLocalDate();
                            LocalDate out = e.getCheckOut().toLocalDate();

                            // Si el día actual cae dentro de la estadía
                            // (Marcamos ocupado desde el día de llegada hasta el día anterior a la salida, o inclusive)
                            if (!fechaActual.isBefore(in) && fechaActual.isBefore(out)) {
                                estadoDia = "OCUPADA";
                                ocupada = true;
                                break;
                            }
                        }
                    }

                    // C. RESERVA (Prioridad 2: Solo si no está ocupada)
                    if (!ocupada) {
                        for (Reserva r : reservasEnRango) {
                            if (r.getHabitacion().getIdHabitacion().equals(habitacion.getIdHabitacion())) {
                                if (!fechaActual.isBefore(r.getFechaIngreso()) && fechaActual.isBefore(r.getFechaEgreso())) {
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
            resultado.add(new HabitacionDisponibilidadDTO(habDTO, dias));
        }

        return resultado;
    }
}


