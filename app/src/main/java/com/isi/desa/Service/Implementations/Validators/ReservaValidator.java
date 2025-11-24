package com.isi.desa.Service.Implementations.Validators;

import com.isi.desa.Dao.Repositories.HabitacionRepository;
import com.isi.desa.Dao.Repositories.ReservaRepository;
import com.isi.desa.Dto.Reserva.CrearReservaRequestDTO;
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
        // 1. Validar Datos básicos
        if (request == null) {
            throw new IllegalArgumentException("La solicitud no puede ser nula.");
        }
        if (request.idsHabitaciones == null || request.idsHabitaciones.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos una habitación.");
        }
        if (request.nombreCliente == null || request.apellidoCliente == null) {
            throw new IllegalArgumentException("Faltan datos del huésped.");
        }

        // 2. Validar Fechas
        LocalDate hoy = LocalDate.now();
        if (request.fechaIngreso.isBefore(hoy)) {
            // Opcional: Permitir reservas pasadas si es un registro histórico
            // throw new IllegalArgumentException("La fecha de ingreso no puede ser en el pasado.");
        }
        if (request.fechaEgreso.isBefore(request.fechaIngreso)) {
            throw new IllegalArgumentException("La fecha de egreso debe ser posterior a la de ingreso.");
        }

        // 3. Validar Disponibilidad de la Habitación
        String idHabitacion = request.idsHabitaciones.get(0);

        // A. ¿Existe la habitación?
        if (!habitacionRepo.existsById(idHabitacion)) {
            throw new IllegalArgumentException("La habitación seleccionada no existe.");
        }

        // B. ¿Está ocupada en esas fechas? (Usamos la query que creamos antes)
        List<Reserva> coincidencias = reservaRepo.findReservasEnRango(
                idHabitacion,
                request.fechaIngreso,
                request.fechaEgreso
        );

        if (!coincidencias.isEmpty()) {
            throw new IllegalArgumentException("La habitación ya se encuentra reservada en el rango de fechas seleccionado.");
        }
    }
}