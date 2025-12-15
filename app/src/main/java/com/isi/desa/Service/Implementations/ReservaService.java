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
            Habitacion habitacion = habitacionRepository.findById(item.getIdHabitacion())
                    .orElseThrow(() -> new RuntimeException("Habitación no encontrada: " + item.getIdHabitacion()));
            // C. Crear Reserva
            Reserva nuevaReserva = new Reserva();

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
        List<Reserva> reservasEnRango = reservaRepository.findReservasEnRango(desde, hasta);
        for (HabitacionResumen h : habitacionesRaw) {

            HabitacionDisponibilidadDTO disponibilidadDTO = new HabitacionDisponibilidadDTO();
            HabitacionDTO habDTO = new HabitacionDTO();

            // --- CORRECCIÓN 1: Convertir Long a String ---
            habDTO.setIdHabitacion(h.getIdHabitacion());
            habDTO.setNumero(h.getNumero());
            habDTO.setPrecio(h.getPrecio());
            habDTO.setCapacidad(h.getCapacidad());

            // --- CORRECCIÓN 2: Manejo del Enum sin forzar un valor por defecto ---
            try {
                // Intenta convertir el texto de la BD al Enum
                habDTO.setTipoHabitacion(TipoHabitacion.valueOf(h.getDetalles().toUpperCase()));
            } catch (Exception e) {
                // Si el texto en la BD no coincide con ningún Enum, lo dejamos nulo o lo logueamos
                // Esto evita el error de compilación si 'INDIVIDUAL' no existe en tu Enum
                habDTO.setTipoHabitacion(null);
                System.out.println("Error mapeando tipo de habitación: " + h.getDetalles());
            }

            disponibilidadDTO.setHabitacion(habDTO);

            // Lógica de Disponibilidad día por día
            List<DisponibilidadDiaDTO> dias = new ArrayList<>();
            LocalDate fechaActual = desde;

            while (!fechaActual.isAfter(hasta)) {

                String estadoDia = "DISPONIBLE";

                if ("MANTENIMIENTO".equals(h.getEstado())) {
                    estadoDia = "MANTENIMIENTO";
                } else {
                    boolean ocupada = false;
                    for (Estadia e : estadiasEnRango) {
                        // Validamos que sea la misma habitación (Comparando Strings ahora)
                        boolean esDeEstaHabitacion = e.getHabitaciones().stream()
                                .anyMatch(habEntidad -> String.valueOf(habEntidad.getIdHabitacion()).equals(String.valueOf(h.getIdHabitacion())));

                        if (esDeEstaHabitacion) {
                            LocalDate checkInDate = e.getCheckIn().toLocalDate();
                            LocalDate checkOutDate = e.getCheckOut().toLocalDate();

                            if (!fechaActual.isBefore(checkInDate) && fechaActual.isBefore(checkOutDate)) {
                                estadoDia = "OCUPADA";
                                ocupada = true;
                                break;
                            }
                        }
                    }
                    if (!ocupada) {
                        for (Reserva r : reservasEnRango) {
                            if (r.getHabitacion().getIdHabitacion().equals(h.getIdHabitacion())) {
                                if (!fechaActual.isBefore(r.getFechaIngreso()) && fechaActual.isBefore(r.getFechaEgreso())) {
                                    estadoDia = "RESERVADA";
                                    break;
                                }
                            }
                        }
                    }
                }

                // --- CORRECCIÓN 3: Usar el constructor con parámetros ---
                // Como no tienes constructor vacío, pasamos los datos al crear el objeto
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