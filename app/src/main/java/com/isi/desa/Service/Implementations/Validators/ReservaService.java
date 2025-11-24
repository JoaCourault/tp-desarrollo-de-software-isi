package com.isi.desa.Service.Implementations;

import com.isi.desa.Dao.Repositories.HabitacionRepository;
import com.isi.desa.Dao.Repositories.HuespedRepository;
import com.isi.desa.Dao.Repositories.ReservaRepository;
import com.isi.desa.Dto.Reserva.CrearReservaRequestDTO;
import com.isi.desa.Model.Entities.Habitacion.HabitacionEntity;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Model.Entities.Reserva.Reserva;
import com.isi.desa.Service.Interfaces.IReservaService;
import com.isi.desa.Service.Interfaces.Validators.IReservaValidator; // <--- Importar Interface Validator
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ReservaService implements IReservaService {

    @Autowired
    private ReservaRepository reservaRepo;
    @Autowired
    private HuespedRepository huespedRepo;
    @Autowired
    private HabitacionRepository habitacionRepo;

    @Autowired
    private IReservaValidator validator; // <--- Inyectamos el validador

    @Override
    @Transactional
    public void crear(CrearReservaRequestDTO request) {

        // 1. EJECUTAR VALIDACIONES (Si algo falla, lanza excepción y se detiene aquí)
        validator.validateCreate(request);

        // 2. Obtener entidad habitación (Ya sabemos que existe gracias al validador)
        String idHabitacion = request.idsHabitaciones.get(0);
        HabitacionEntity habitacion = habitacionRepo.findById(idHabitacion).get();

        // 3. Crear Huésped "Rápido"
        Huesped huesped = new Huesped();
        huesped.setIdHuesped("HU-" + UUID.randomUUID().toString().substring(0, 8));
        huesped.setNombre(request.nombreCliente);
        huesped.setApellido(request.apellidoCliente);
        huesped.setTelefono(request.telefonoCliente);

        huesped.setNumDoc("TEMP-" + System.currentTimeMillis());
        huesped.setTipoDocumento("DNI");
        huesped.setEliminado(false);

        huesped = huespedRepo.save(huesped);

        // 4. Crear Reserva
        Reserva reserva = new Reserva();
        reserva.setIdReserva("RES-" + UUID.randomUUID().toString().substring(0, 8));
        reserva.setHuesped(huesped);
        reserva.setHabitacion(habitacion);
        reserva.setFechaDesde(request.fechaIngreso);
        reserva.setFechaHasta(request.fechaEgreso);

        reserva.setFechaIngreso(request.fechaIngreso);
        reserva.setFechaEgreso(request.fechaEgreso);
        reserva.setEstado("RESERVADA");

        reservaRepo.save(reserva);
    }
}