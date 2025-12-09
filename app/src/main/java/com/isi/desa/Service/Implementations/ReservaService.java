package com.isi.desa.Service.Implementations;

import com.isi.desa.Dao.Repositories.HabitacionRepository;
import com.isi.desa.Dao.Repositories.ReservaRepository;
import com.isi.desa.Dto.Reserva.CrearReservaRequestDTO;
import com.isi.desa.Dto.Reserva.ReservaDetalleDTO;
import com.isi.desa.Model.Entities.Habitacion.HabitacionEntity;
import com.isi.desa.Model.Entities.Reserva.Reserva;
import com.isi.desa.Service.Interfaces.IReservaService;
import com.isi.desa.Service.Interfaces.Validators.IReservaValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ReservaService implements IReservaService {

    @Autowired private ReservaRepository reservaRepo;
    @Autowired private HabitacionRepository habitacionRepo;
    @Autowired private IReservaValidator validator;

    @Override
    @Transactional
    public void crear(CrearReservaRequestDTO request) {

        validator.validateCreate(request);

        for (ReservaDetalleDTO detalle : request.reservas) {

            HabitacionEntity habitacion = habitacionRepo.findById(detalle.idHabitacion)
                    .orElseThrow(() -> new RuntimeException("Habitación no encontrada: " + detalle.idHabitacion));

            // Crear la entidad Reserva
            Reserva reserva = new Reserva();

            reserva.setNombreCliente(request.nombreCliente);
            reserva.setApellidoCliente(request.apellidoCliente);
            reserva.setTelefonoCliente(request.telefonoCliente);

            reserva.setHuesped(null);

            reserva.setHabitacion(habitacion);

            // Fechas
            reserva.setFechaDesde(detalle.fechaDesde);
            reserva.setFechaHasta(detalle.fechaHasta);
            reserva.setFechaIngreso(detalle.fechaDesde);
            reserva.setFechaEgreso(detalle.fechaHasta);

            reserva.setEstado("RESERVADA");

            reservaRepo.save(reserva);
        }
    }
}