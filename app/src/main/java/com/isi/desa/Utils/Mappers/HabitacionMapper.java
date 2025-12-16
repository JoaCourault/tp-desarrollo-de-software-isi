package com.isi.desa.Utils.Mappers;

import com.isi.desa.Dto.Habitacion.HabitacionDTO;
import com.isi.desa.Model.Entities.Habitacion.Habitacion;
import org.springframework.stereotype.Component;

@Component
public class HabitacionMapper {

    public HabitacionDTO toDTO(Habitacion entity) {
        if (entity == null) {
            return null;
        }

        HabitacionDTO dto = new HabitacionDTO();

        dto.setIdHabitacion(entity.getIdHabitacion());
        dto.setNumero(entity.getNumero());
        dto.setPiso(entity.getPiso());
        dto.setPrecio(entity.getPrecio());

        // Manejo de Enums a String para el DTO
        if (entity.getEstado() != null) {
            dto.setEstado(entity.getEstado());
        }
        if (entity.getTipoHabitacion() != null) {
            dto.setTipoHabitacion(entity.getTipoHabitacion());
        }

        dto.setCapacidad(entity.getCapacidad());
        dto.setDetalles(entity.getDetalles());

        return dto;
    }
}
