package com.isi.desa.Utils.Mappers;

import com.isi.desa.Dto.Reserva.ReservaDTO;
import com.isi.desa.Model.Entities.Reserva.Reserva;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper de Reserva <-> DTO
 * Importante: NO setea relaciones con otras entidades
 * (habitacion, huesped) porque eso se resuelve en el DAO.
 */
public class ReservaMapper {

    // ===== DTO → ENTITY =====
    public static Reserva dtoToEntity(ReservaDTO dto) {
        if (dto == null) return null;

        Reserva entity = new Reserva();

        entity.setIdReserva(dto.idReserva);
        entity.setNombreCliente(dto.nombreCliente);
        entity.setApellidoCliente(dto.apellidoCliente);
        entity.setTelefonoCliente(dto.telefonoCliente);

        entity.setEstado(dto.estado);

        entity.setFechaDesde(dto.fechaDesde);
        entity.setFechaHasta(dto.fechaHasta);
        entity.setFechaIngreso(dto.fechaIngreso);
        entity.setFechaEgreso(dto.fechaEgreso);

        // NOTA:
        // idHabitacion (y si hubiera huesped) NO se asignan aquí.
        // Las relaciones se setean en ReservaDAO.

        return entity;
    }

    // ===== ENTITY → DTO =====
    public static ReservaDTO entityToDTO(Reserva entity) {
        if (entity == null) return null;

        ReservaDTO dto = new ReservaDTO();

        dto.idReserva = entity.getIdReserva();
        dto.nombreCliente = entity.getNombreCliente();
        dto.apellidoCliente = entity.getApellidoCliente();
        dto.telefonoCliente = entity.getTelefonoCliente();

        dto.estado = entity.getEstado();

        dto.fechaDesde = entity.getFechaDesde();
        dto.fechaHasta = entity.getFechaHasta();
        dto.fechaIngreso = entity.getFechaIngreso();
        dto.fechaEgreso = entity.getFechaEgreso();

        // Relaciones: solo devolvemos ID, no la entidad completa
        if (entity.getHabitacion() != null) {
            dto.idHabitacion = entity.getHabitacion().getIdHabitacion();
        }

        return dto;
    }

    // ===== ENTITY LIST → DTO LIST =====
    public static List<ReservaDTO> entityListToDTOList(List<Reserva> entities) {
        List<ReservaDTO> dtos = new ArrayList<>();
        if (entities == null) return dtos;

        for (Reserva r : entities) {
            dtos.add(entityToDTO(r));
        }
        return dtos;
    }

    // ===== UPDATE ENTITY FROM DTO =====
    /**
     * Actualiza los campos simples de una Reserva existente
     * con los valores del DTO. No toca relaciones.
     */
    public static Reserva updateEntityFromDTO(Reserva entity, ReservaDTO dto) {
        if (entity == null || dto == null) return entity;

        // ID: si el DTO trae uno, lo respetamos, si no, dejamos el existente
        if (dto.idReserva != null && !dto.idReserva.isBlank()) {
            entity.setIdReserva(dto.idReserva);
        }

        entity.setNombreCliente(dto.nombreCliente);
        entity.setApellidoCliente(dto.apellidoCliente);
        entity.setTelefonoCliente(dto.telefonoCliente);

        entity.setEstado(dto.estado);

        entity.setFechaDesde(dto.fechaDesde);
        entity.setFechaHasta(dto.fechaHasta);
        entity.setFechaIngreso(dto.fechaIngreso);
        entity.setFechaEgreso(dto.fechaEgreso);

        // Relaciones (habitacion / huesped) se manejan en el DAO
        return entity;
    }
}
