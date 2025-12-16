package com.isi.desa.Service.Implementations.Validators;

import com.isi.desa.Dao.Repositories.HabitacionRepository;
import com.isi.desa.Dao.Repositories.ReservaRepository;
import com.isi.desa.Dto.Reserva.CrearReservaRequestDTO;
import com.isi.desa.Dto.Reserva.ReservaDetalleDTO;
import com.isi.desa.Model.Entities.Reserva.Reserva;
import com.isi.desa.Service.Interfaces.Validators.IReservaValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class ReservaValidator implements IReservaValidator {

    @Autowired
    private HabitacionRepository habitacionRepo;

    @Autowired
    private ReservaRepository reservaRepo;

    @Override
    public void validateCreate(CrearReservaRequestDTO request) {

        if (request == null) {
            throw new IllegalArgumentException("La solicitud no puede ser nula.");
        }

        if (request.nombreCliente == null || request.nombreCliente.trim().isEmpty()
                || request.apellidoCliente == null || request.apellidoCliente.trim().isEmpty()) {
            throw new IllegalArgumentException("Faltan datos del huésped (Nombre y Apellido son obligatorios).");
        }

        if (request.reservas == null || request.reservas.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos una habitación.");
        }

        LocalDate hoy = LocalDate.now();

        for (ReservaDetalleDTO detalle : request.reservas) {

            if (detalle.idHabitacion == null || detalle.idHabitacion.trim().isEmpty()) {
                throw new IllegalArgumentException("Hay una reserva sin habitación seleccionada.");
            }
            if (detalle.fechaDesde == null || detalle.fechaHasta == null) {
                throw new IllegalArgumentException("Las fechas de reserva son obligatorias.");
            }

            if (detalle.fechaDesde.isBefore(hoy)) {
                // Si querés bloquear pasado, descomentá:
                // throw new IllegalArgumentException("No se puede reservar en el pasado (Habitación " + detalle.idHabitacion + ").");
            }

            if (!detalle.fechaHasta.isAfter(detalle.fechaDesde)) {
                throw new IllegalArgumentException("La fecha de salida debe ser posterior a la de entrada.");
            }

            if (!habitacionRepo.existsById(detalle.idHabitacion)) {
                throw new IllegalArgumentException("La habitación con ID " + detalle.idHabitacion + " no existe.");
            }

            // ✅ Conversión para solapamiento en TIMESTAMP (misma lógica que usás en service)
            LocalDateTime desde = detalle.fechaDesde.atTime(14, 0); // check-in
            LocalDateTime hasta = detalle.fechaHasta.atTime(10, 0); // check-out

            List<Reserva> coincidencias = reservaRepo.findReservasEnRango(
                    detalle.idHabitacion,
                    desde,
                    hasta
            );

            boolean ocupada = coincidencias.stream()
                    .anyMatch(r -> !"CANCELADA".equalsIgnoreCase(r.getEstado()));

            if (ocupada) {
                throw new IllegalArgumentException("La habitación " + detalle.idHabitacion +
                        " no está disponible entre " + detalle.fechaDesde + " y " + detalle.fechaHasta);
            }
        }
    }
}
