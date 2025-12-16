package com.isi.desa.Utils.Mappers;

import com.isi.desa.Dto.Reserva.ReservaDTO;
import com.isi.desa.Model.Entities.Reserva.Reserva;

import java.util.ArrayList;
import java.util.List;

public class ReservaMapper {

    public static Reserva dtoToEntity(ReservaDTO dto) {
        if (dto == null) return null;

        Reserva entity = new Reserva();

        entity.setIdReserva(dto.idReserva);

        entity.setNombreHuesped(dto.nombreCliente);
        entity.setApellidoHuesped(dto.apellidoCliente);
        entity.setTelefonoHuesped(dto.telefonoCliente);

        entity.setEstado(dto.estado);

        // Planificación (LocalDate) -> se convierte internamente a TIMESTAMP
        entity.setFechaDesde(dto.fechaDesde);
        entity.setFechaHasta(dto.fechaHasta);

        // Fechas reales (si vienen explícitas, pisan el default)
        if (dto.fechaIngreso != null) entity.setFechaIngreso(dto.fechaIngreso);
        if (dto.fechaEgreso != null) entity.setFechaEgreso(dto.fechaEgreso);

        return entity;
    }

    public static ReservaDTO entityToDTO(Reserva entity) {
        if (entity == null) return null;

        ReservaDTO dto = new ReservaDTO();

        dto.idReserva = entity.getIdReserva();

        dto.nombreCliente = entity.getNombreHuesped();
        dto.apellidoCliente = entity.getApellidoHuesped();
        dto.telefonoCliente = entity.getTelefonoHuesped();

        dto.estado = entity.getEstado();

        dto.fechaDesde = entity.getFechaDesde();
        dto.fechaHasta = entity.getFechaHasta();

        dto.fechaIngreso = entity.getFechaIngreso();
        dto.fechaEgreso = entity.getFechaEgreso();

        if (entity.getHabitacion() != null) {
            dto.idHabitacion = entity.getHabitacion().getIdHabitacion();
        }

        return dto;
    }

    public static List<ReservaDTO> entityListToDTOList(List<Reserva> entities) {
        List<ReservaDTO> dtos = new ArrayList<>();
        if (entities == null) return dtos;
        for (Reserva r : entities) {
            dtos.add(entityToDTO(r));
        }
        return dtos;
    }

    public static Reserva updateEntityFromDTO(Reserva entity, ReservaDTO dto) {
        if (entity == null || dto == null) return entity;

        if (dto.idReserva != null && !dto.idReserva.isBlank()) {
            entity.setIdReserva(dto.idReserva);
        }

        entity.setNombreHuesped(dto.nombreCliente);
        entity.setApellidoHuesped(dto.apellidoCliente);
        entity.setTelefonoHuesped(dto.telefonoCliente);

        entity.setEstado(dto.estado);

        // LocalDate (planificación) -> ajusta TIMESTAMP con hora default
        entity.setFechaDesde(dto.fechaDesde);
        entity.setFechaHasta(dto.fechaHasta);

        // Si vienen fechas reales explícitas, pisan lo anterior
        if (dto.fechaIngreso != null) entity.setFechaIngreso(dto.fechaIngreso);
        if (dto.fechaEgreso != null) entity.setFechaEgreso(dto.fechaEgreso);

        return entity;
    }
}
