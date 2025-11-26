package com.isi.desa.Service.Implementations.Validators;

import com.isi.desa.Dao.Repositories.HabitacionRepository;
import com.isi.desa.Dao.Repositories.ReservaRepository;
import com.isi.desa.Dto.Reserva.CrearReservaRequestDTO;
import com.isi.desa.Dto.Reserva.ReservaHabitacionDTO;
import com.isi.desa.Model.Entities.Reserva.Reserva;
import com.isi.desa.Service.Interfaces.Validators.IReservaValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReservaValidator implements IReservaValidator {

    @Autowired
    private HabitacionRepository habitacionRepo;

    @Autowired
    private ReservaRepository reservaRepo;

    @Override
    public void validateCreate(CrearReservaRequestDTO request) {

        // === 1. DATOS DEL CLIENTE ===
        if (request.nombreCliente == null || request.nombreCliente.isBlank()) {
            throw new RuntimeException("El nombre del cliente es obligatorio.");
        }

        if (request.apellidoCliente == null || request.apellidoCliente.isBlank()) {
            throw new RuntimeException("El apellido del cliente es obligatorio.");
        }

        if (request.telefonoCliente == null || request.telefonoCliente.isBlank()) {
            throw new RuntimeException("El teléfono del cliente es obligatorio.");
        }

        // === 2. VALIDAR LISTA DE RESERVAS ===
        if (request.reservas == null || request.reservas.isEmpty()) {
            throw new RuntimeException("Debe seleccionar al menos una habitación.");
        }

        // === 3. VALIDAR CADA RESERVA INDIVIDUAL ===
        for (ReservaHabitacionDTO r : request.reservas) {

            if (r.idHabitacion == null || r.idHabitacion.isBlank()) {
                throw new RuntimeException("Falta idHabitacion en una de las reservas.");
            }

            if (!habitacionRepo.existsById(r.idHabitacion)) {
                throw new RuntimeException("La habitación no existe: " + r.idHabitacion);
            }

            if (r.fechaDesde == null || r.fechaHasta == null) {
                throw new RuntimeException("Faltan fechas para la habitación " + r.idHabitacion);
            }

            if (r.fechaHasta.isBefore(r.fechaDesde)) {
                throw new RuntimeException(
                        "La fechaHasta no puede ser anterior a fechaDesde en la habitación " + r.idHabitacion
                );
            }

            // === 4. VALIDAR SOLAPAMIENTO CON RESERVAS EXISTENTES ===
            List<Reserva> reservasSolapadas = reservaRepo.findReservasEnRango(
                    r.idHabitacion,
                    r.fechaDesde,
                    r.fechaHasta
            );

            if (!reservasSolapadas.isEmpty()) {
                throw new RuntimeException(
                        "La habitación " + r.idHabitacion + " ya está reservada en el rango solicitado."
                );
            }
        }
    }
}
