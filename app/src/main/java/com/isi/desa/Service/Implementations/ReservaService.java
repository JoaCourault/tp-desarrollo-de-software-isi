package com.isi.desa.Service.Implementations;

import com.isi.desa.Dao.Interfaces.IReservaDAO;
import com.isi.desa.Dao.Repositories.HabitacionRepository;
import com.isi.desa.Dto.Reserva.CrearReservaRequestDTO;
import com.isi.desa.Dto.Reserva.ReservaDetalleDTO;
import com.isi.desa.Dto.Reserva.ReservaDTO;
import com.isi.desa.Model.Entities.Habitacion.HabitacionEntity;
import com.isi.desa.Model.Entities.Reserva.Reserva;
import com.isi.desa.Service.Interfaces.IReservaService;
import com.isi.desa.Service.Interfaces.Validators.IReservaValidator;
import com.isi.desa.Utils.Mappers.ReservaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ReservaService implements IReservaService {

    @Autowired
    private IReservaValidator validator;

    @Autowired
    @Qualifier("reservaDAO")
    private IReservaDAO reservaDAO;

    @Autowired
    private HabitacionRepository habitacionRepo;

    @Override
    @Transactional
    public void crear(CrearReservaRequestDTO request) {

        // 1) Validar request completa (como ya hacías)
        validator.validateCreate(request);

        // 2) Por cada detalle creamos una Reserva independiente
        for (ReservaDetalleDTO detalle : request.reservas) {

            // Armamos DTO (igual que antes)
            ReservaDTO dto = new ReservaDTO();
            dto.nombreCliente = request.nombreCliente;
            dto.apellidoCliente = request.apellidoCliente;
            dto.telefonoCliente = request.telefonoCliente;

            dto.idHabitacion = detalle.idHabitacion;

            dto.fechaDesde = detalle.fechaDesde;
            dto.fechaHasta = detalle.fechaHasta;
            dto.fechaIngreso = detalle.fechaDesde;
            dto.fechaEgreso = detalle.fechaHasta;

            dto.estado = "RESERVADA";

            // 3) Convertimos a entidad
            Reserva reserva = ReservaMapper.dtoToEntity(dto);

            // 4) Generar ID (FORMATO DEFINITIVO)
            if (reserva.getIdReserva() == null || reserva.getIdReserva().isBlank()) {
                String uuidRes = UUID.randomUUID().toString().replace("-", "");
                reserva.setIdReserva("RE_" + uuidRes.substring(0, 15));
            }

            // 5) Resolver relación habitación (service, no DAO)
            HabitacionEntity hab = habitacionRepo.findById(detalle.idHabitacion)
                    .orElseThrow(() -> new RuntimeException("Habitación no encontrada: " + detalle.idHabitacion));
            reserva.setHabitacion(hab);

            // 6) Persistencia con save()
            reservaDAO.save(reserva);
        }
    }
}
