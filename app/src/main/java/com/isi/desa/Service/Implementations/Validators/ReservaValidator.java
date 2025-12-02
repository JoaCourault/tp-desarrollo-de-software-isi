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
import java.util.List;

@Component
public class ReservaValidator implements IReservaValidator {

    @Autowired
    private HabitacionRepository habitacionRepo;

    @Autowired
    private ReservaRepository reservaRepo;

    @Override
    public void validateCreate(CrearReservaRequestDTO request) {

        // 1. Validar Objeto principal
        if (request == null) {
            throw new IllegalArgumentException("La solicitud no puede ser nula.");
        }

        // 2. Validar Datos del Cliente
        if (request.nombreCliente == null || request.nombreCliente.trim().isEmpty() ||
                request.apellidoCliente == null || request.apellidoCliente.trim().isEmpty()) {
            throw new IllegalArgumentException("Faltan datos del huésped (Nombre y Apellido son obligatorios).");
        }

        // 3. Validar Lista de Reservas (El error que te salía antes)
        if (request.reservas == null || request.reservas.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos una habitación.");
        }

        // 4. Validar CADA reserva individualmente (Fechas y Disponibilidad)
        LocalDate hoy = LocalDate.now();

        for (ReservaDetalleDTO detalle : request.reservas) {

            // A. Validar integridad de datos por ítem
            if (detalle.idHabitacion == null) {
                throw new IllegalArgumentException("Hay una reserva sin habitación seleccionada.");
            }
            if (detalle.fechaDesde == null || detalle.fechaHasta == null) {
                throw new IllegalArgumentException("Las fechas de reserva son obligatorias.");
            }

            // B. Validar Lógica de Fechas
            if (detalle.fechaDesde.isBefore(hoy)) {
                // throw new IllegalArgumentException("No se puede reservar en el pasado (Habitación " + detalle.idHabitacion + ").");
            }
            if (detalle.fechaHasta.isBefore(detalle.fechaDesde) || detalle.fechaHasta.isEqual(detalle.fechaDesde)) {
                throw new IllegalArgumentException("La fecha de salida debe ser posterior a la de entrada.");
            }

            // C. Validar Existencia de Habitación
            if (!habitacionRepo.existsById(detalle.idHabitacion)) {
                throw new IllegalArgumentException("La habitación con ID " + detalle.idHabitacion + " no existe.");
            }

            // D. Validar Disponibilidad en BD (Solapamiento)
            List<Reserva> coincidencias = reservaRepo.findReservasEnRango(
                    detalle.idHabitacion,
                    detalle.fechaDesde,
                    detalle.fechaHasta
            );

            // Filtramos para ignorar reservas canceladas si tuvieras ese estado
            // Por ahora asumimos que si está en BD y choca fechas, es conflicto.
            boolean ocupada = coincidencias.stream()
                    .anyMatch(r -> !"CANCELADA".equalsIgnoreCase(r.getEstado()));

            if (ocupada) {
                throw new IllegalArgumentException("La habitación " + detalle.idHabitacion +
                        " no está disponible entre " + detalle.fechaDesde + " y " + detalle.fechaHasta);
            }
        }
    }
}