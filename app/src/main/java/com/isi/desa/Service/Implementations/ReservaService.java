package com.isi.desa.Service.Implementations;

import com.isi.desa.Dao.Repositories.HuespedRepository;
import com.isi.desa.Dao.Repositories.HabitacionRepository;
import com.isi.desa.Dao.Repositories.ReservaRepository;

import com.isi.desa.Dto.Reserva.CrearReservaRequestDTO;
import com.isi.desa.Dto.Reserva.ReservaHabitacionDTO;

import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Model.Entities.Habitacion.HabitacionEntity;
import com.isi.desa.Model.Entities.Reserva.Reserva;
import com.isi.desa.Service.Interfaces.IReservaService;

import com.isi.desa.Service.Interfaces.Validators.IReservaValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.time.LocalDate;


@Service
public class ReservaService implements IReservaService {

    @Autowired
    private HuespedRepository huespedRepo;

    @Autowired
    private HabitacionRepository habitacionRepo;

    @Autowired
    private ReservaRepository reservaRepo;

    @Autowired
    private IReservaValidator reservaValidator;


    @Override
    public void crear(CrearReservaRequestDTO request) {
        reservaValidator.validateCreate(request);

        // 1. Crear HUESPED EVENTUAL
        Huesped h = new Huesped();
        h.setIdHuesped(UUID.randomUUID().toString());
        h.setNombre(request.nombreCliente);
        h.setApellido(request.apellidoCliente);
        h.setTelefono(request.telefonoCliente);

        h.setTipoDocumento("EVENTUAL");
        h.setNumDoc("-");
        h.setPosicionIva("Consumidor Final");
        h.setEliminado(false);

        huespedRepo.save(h);

        // 2. Crear una reserva por cada habitación
        for (ReservaHabitacionDTO r : request.reservas) {

            HabitacionEntity habitacion = habitacionRepo.findById(r.idHabitacion)
                    .orElseThrow(() -> new RuntimeException("Habitación no encontrada: " + r.idHabitacion));

            Reserva reserva = new Reserva();
            reserva.setIdReserva(UUID.randomUUID().toString());

            reserva.setHuesped(h);
            reserva.setHabitacion(habitacion);

            reserva.setFechaDesde(r.fechaDesde);
            reserva.setFechaHasta(r.fechaHasta);

            reserva.setFechaIngreso(r.fechaDesde);
            reserva.setFechaEgreso(r.fechaHasta);

            reserva.setEstado("RESERVADA");

            reservaRepo.save(reserva);
        }
    }
}

