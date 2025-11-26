package com.isi.desa.Utils.Mappers;

import com.isi.desa.Dto.Habitacion.HabitacionDTO;
import com.isi.desa.Model.Entities.Habitacion.HabitacionEntity;

import java.math.BigDecimal;

public class HabitacionMapper {

    public static HabitacionDTO entityToDTO(HabitacionEntity e) {
        HabitacionDTO dto = new HabitacionDTO();

        dto.id_habitacion = e.getIdHabitacion();
        dto.precio = e.getPrecio() != null ? e.getPrecio().floatValue() : null;
        dto.numero = e.getNumero();
        dto.piso = e.getPiso();
        dto.capacidad = e.getCapacidad();
        dto.detalles = e.getDetalles();
        dto.tipoHabitacion = e.getTipoHabitacion();
        dto.cantidadCamasIndividual = e.getCantidadCamasIndividual();
        dto.cantidadCamasDobles = e.getCantidadCamasDobles();
        dto.cantidadCamasKingSize = e.getCantidadCamasKingSize();
        dto.estado = e.getEstado();

        return dto;
    }

    public static HabitacionEntity dtoToEntity(HabitacionDTO dto) {
        HabitacionEntity e = new HabitacionEntity();

        e.setIdHabitacion(dto.id_habitacion);
        e.setPrecio(dto.precio != null ? BigDecimal.valueOf(dto.precio) : null);
        e.setNumero(dto.numero);
        e.setPiso(dto.piso);
        e.setCapacidad(dto.capacidad);
        e.setDetalles(dto.detalles);
        e.setTipoHabitacion(dto.tipoHabitacion);
        e.setCantidadCamasIndividual(dto.cantidadCamasIndividual);
        e.setCantidadCamasDobles(dto.cantidadCamasDobles);
        e.setCantidadCamasKingSize(dto.cantidadCamasKingSize);
        e.setEstado(dto.estado);

        return e;
    }
}
