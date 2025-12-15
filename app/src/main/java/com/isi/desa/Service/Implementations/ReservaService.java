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

    @Override
    @Transactional
    public void realizarReserva(CrearReservaRequestDTO request) {

        // Validaciones básicas de entrada
        if (request.getReservas() == null || request.getReservas().isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos una habitación.");
        }

        // --- BUCLE (Coincide con 'loop para cada Habitacion' del diagrama) ---
        for (ReservaDetalleDTO item : request.getReservas()) {

            // 1. Validaciones de Fechas (Refleja la validación de UI del diagrama pero en backend)
            if (item.getFechaDesde().isAfter(item.getFechaHasta())) {
                throw new IllegalArgumentException("Fecha ingreso mayor a egreso en habitación " + item.getIdHabitacion());
            }
            if (item.getFechaDesde().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("No se puede reservar en una fecha anterior al día de hoy.");
            }

            // 2. Buscar Habitación (Paso previo necesario para setear la relación)
            Habitacion habitacion = habitacionRepository.findById(item.getIdHabitacion())
                    .orElseThrow(() -> new RuntimeException("Habitación no encontrada..."));

            // 3. create Reserva (Según diagrama)
            Reserva nuevaReserva = new Reserva();

            // 4. Setters (Según diagrama)
            // IMPORTANTE: NO setear ID manualmente, dejar que @GeneratedValue actúe.

            // Seteo de Fechas
            nuevaReserva.setFechaIngreso(item.getFechaDesde().atStartOfDay());
            nuevaReserva.setFechaEgreso(item.getFechaHasta().atStartOfDay());

            // Seteo de Habitación
            nuevaReserva.setHabitacion(habitacion);

            // Seteo de Datos del Huésped (Diagrama: Ingresar Apellido, Nombre, Telefono)
            nuevaReserva.setNombreHuesped(request.getNombreCliente());
            nuevaReserva.setApellidoHuesped(request.getApellidoCliente());
            nuevaReserva.setTelefonoHuesped(request.getTelefonoCliente());

            // 5. Guardar (RDAO -> GR: confirmacion OK)
            reservaDAO.guardar(nuevaReserva);
        }
        // Fin del loop. El controlador retornará el mensaje de éxito.
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
                                ocupada = true;
                                break;
                            }
                        }
                    }
                    if (ocupada) {
                        estadoDia = "RESERVADO"; // O "OCUPADA" según tu DTO
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
}