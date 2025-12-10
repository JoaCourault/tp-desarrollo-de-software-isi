package com.isi.desa.Utils.Mappers;

import com.isi.desa.Dto.Habitacion.HabitacionDTO;
import com.isi.desa.Model.Entities.Habitacion.HabitacionEntity;

import java.util.ArrayList;
import java.util.List;

public class HabitacionMapper {

    public static HabitacionDTO entityToDTO(HabitacionEntity e) {
        if (e == null) return null;
        HabitacionDTO dto = new HabitacionDTO();

        // Usamos idHabitacion (camelCase)
        dto.id_habitacion = e.getIdHabitacion();

        // Asignación directa de BigDecimal (sin convertir a Float)
        dto.precio = e.getPrecio();

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
        if (dto == null) return null;
        HabitacionEntity e = new HabitacionEntity();

        e.setIdHabitacion(dto.id_habitacion);

        // Asignación directa de BigDecimal
        e.setPrecio(dto.precio);

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

    // Método extra útil para listas
    public static List<HabitacionDTO> entityListToDtoList(List<HabitacionEntity> entities) {
        if (entities == null) return new ArrayList<>();
        List<HabitacionDTO> dtos = new ArrayList<>();
        for (HabitacionEntity h : entities) {
            dtos.add(entityToDTO(h));
        }
        return dtos;
    }
}