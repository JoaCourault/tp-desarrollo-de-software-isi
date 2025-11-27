package com.isi.desa.Service.Implementations;

import com.isi.desa.Dao.Repositories.HabitacionRepository;
import com.isi.desa.Dao.Repositories.ReservaRepository;

import com.isi.desa.Dto.Reserva.CrearReservaRequestDTO;
import com.isi.desa.Dto.Reserva.ReservaHabitacionDTO;

import com.isi.desa.Model.Entities.Habitacion.HabitacionEntity;
import com.isi.desa.Model.Entities.Reserva.Reserva;

import com.isi.desa.Service.Interfaces.IReservaService;
import com.isi.desa.Service.Interfaces.Validators.IReservaValidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReservaService implements IReservaService {

    @Autowired
    private HabitacionRepository habitacionRepo;

    @Autowired
    private ReservaRepository reservaRepo;

    @Autowired
    private IReservaValidator reservaValidator;

    @Override
    public void crear(CrearReservaRequestDTO request) {

        reservaValidator.validateCreate(request);

        for (ReservaHabitacionDTO r : request.reservas) {

            HabitacionEntity habitacion = habitacionRepo.findById(r.idHabitacion)
                    .orElseThrow(() -> new RuntimeException("Habitación no encontrada: " + r.idHabitacion));

            Reserva reserva = new Reserva(
                    habitacion,
                    r.fechaDesde,
                    r.fechaHasta,
                    request.nombreCliente,
                    request.apellidoCliente,
                    request.telefonoCliente
            );

            reservaRepo.save(reserva);
        }
    }
}
