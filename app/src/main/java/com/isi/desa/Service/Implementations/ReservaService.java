package com.isi.desa.Service.Implementations;

import com.isi.desa.Dao.Interfaces.IReservaDAO;
import com.isi.desa.Dto.Reserva.CrearReservaRequestDTO;
import com.isi.desa.Dto.Reserva.ReservaDetalleDTO;
import com.isi.desa.Dto.Reserva.ReservaDTO;
import com.isi.desa.Service.Interfaces.IReservaService;
import com.isi.desa.Service.Interfaces.Validators.IReservaValidator;
import com.isi.desa.Model.Entities.Reserva.Reserva;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservaService implements IReservaService {

    @Autowired
    private IReservaValidator validator;

    @Autowired
    @Qualifier("reservaDAO")
    private IReservaDAO reservaDAO;

    @Override
    @Transactional
    public void crear(CrearReservaRequestDTO request) {

        // 1) Validar request completa (cliente + lista de reservas)
        validator.validateCreate(request);

        // 2) Por cada detalle creamos una Reserva independiente
        for (ReservaDetalleDTO detalle : request.reservas) {

            ReservaDTO dto = new ReservaDTO();

            // Datos del cliente (comunes a todas las reservas del request)
            dto.nombreCliente   = request.nombreCliente;
            dto.apellidoCliente = request.apellidoCliente;
            dto.telefonoCliente = request.telefonoCliente;// si existe en tu DTO

            // Datos de la habitación (se resuelven en DAO con repositorio)
            dto.idHabitacion = detalle.idHabitacion;

            // Fechas
            dto.fechaDesde   = detalle.fechaDesde;
            dto.fechaHasta   = detalle.fechaHasta;
            dto.fechaIngreso = detalle.fechaDesde;
            dto.fechaEgreso  = detalle.fechaHasta;

            // Estado inicial
            dto.estado = "RESERVADA";

            // 3) Persistencia vía DAO (el DAO se encarga del ID y las relaciones)
            Reserva creada = reservaDAO.crear(dto);
            // Si necesitás devolver algo, podés acumular resultados aquí
        }
    }
}
