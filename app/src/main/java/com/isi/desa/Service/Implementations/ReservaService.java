package com.isi.desa.Service.Implementations;

import com.isi.desa.Dao.Interfaces.IReservaDAO;
import com.isi.desa.Dao.Repositories.HabitacionRepository;
import com.isi.desa.Dto.Reserva.CrearReservaRequestDTO;
import com.isi.desa.Dto.Reserva.ReservaDetalleDTO;
import com.isi.desa.Dto.Reserva.ReservaDTO;
import com.isi.desa.Model.Entities.Habitacion.Habitacion;
import com.isi.desa.Model.Entities.Reserva.Reserva;
import com.isi.desa.Service.Interfaces.IReservaService;
import com.isi.desa.Service.Interfaces.Validators.IReservaValidator;
import com.isi.desa.Utils.Mappers.ReservaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
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

        validator.validateCreate(request);

        for (ReservaDetalleDTO detalle : request.reservas) {

            ReservaDTO dto = new ReservaDTO();
            dto.nombreCliente = request.nombreCliente;
            dto.apellidoCliente = request.apellidoCliente;
            dto.telefonoCliente = request.telefonoCliente;

            dto.idHabitacion = detalle.idHabitacion;

            // Fechas de Planificación (LocalDate)
            dto.fechaDesde = detalle.fechaDesde;
            dto.fechaHasta = detalle.fechaHasta;

            // Fechas Reales (LocalDateTime) - Inicializamos con hora CheckIn/Out por defecto
            // CheckIn: 14:00, CheckOut: 10:00 (Ejemplo estándar)
            if (detalle.fechaDesde != null) {
                dto.fechaIngreso = detalle.fechaDesde.atTime(14, 0);
            }
            if (detalle.fechaHasta != null) {
                dto.fechaEgreso = detalle.fechaHasta.atTime(10, 0);
            }

            dto.estado = "RESERVADA";

            Reserva reserva = ReservaMapper.dtoToEntity(dto);

            if (reserva.getIdReserva() == null || reserva.getIdReserva().isBlank()) {
                String uuidRes = UUID.randomUUID().toString().replace("-", "");
                reserva.setIdReserva("RE_" + uuidRes.substring(0, 15));
            }

            Habitacion hab = habitacionRepo.findById(detalle.idHabitacion)
                    .orElseThrow(() -> new RuntimeException("Habitación no encontrada: " + detalle.idHabitacion));
            reserva.setHabitacion(hab);

            reservaDAO.save(reserva);
        }
    }
}