package com.isi.desa.Utils.Mappers;

import com.isi.desa.Dto.Habitacion.HabitacionDTO;
import com.isi.desa.Model.Entities.Habitacion.Habitacion;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HabitacionMapper {

    public static List<HabitacionDTO> entityListToDtoList(List<Habitacion> habitaciones) {
        if (habitaciones == null) {
            return null;
        }
        HabitacionMapper mapper = new HabitacionMapper();
        return habitaciones.stream()
                .map(mapper::toDTO)
                .toList();
    }

    public static List<Habitacion> dtoLisToEntitiesList(List<HabitacionDTO> habitaciones) {
        if (habitaciones == null) {
            return null;
        }
        HabitacionMapper mapper = new HabitacionMapper();
        return habitaciones.stream()
                .map(mapper::toEntity)
                .toList();
    }

    public Habitacion toEntity(HabitacionDTO habitacionDTO) {
        if (habitacionDTO == null) {
            return null;
        }

        Habitacion entity = null;
        com.isi.desa.Model.Enums.TipoHabitacion tipo = habitacionDTO.getTipoHabitacion();
        if (tipo != null) {
            switch (tipo) {
                case DOBLE_ESTANDAR -> entity = new com.isi.desa.Model.Entities.Habitacion.DobleEstandar();
                case DOBLE_SUPERIOR -> entity = new com.isi.desa.Model.Entities.Habitacion.DobleSuperior();
                case INDIVIDUAL_ESTANDAR -> entity = new com.isi.desa.Model.Entities.Habitacion.IndividualEstandar();
                case SUITE_DOBLE -> entity = new com.isi.desa.Model.Entities.Habitacion.SuiteDoble();
                case SUPERIOR_FAMILY_PLAN -> entity = new com.isi.desa.Model.Entities.Habitacion.SuperiorFamilyPlan();
            }
        }
        if (entity == null) {
            // Si no se reconoce el tipo, no se puede instanciar una habitación abstracta
            return null;
        }

        entity.setIdHabitacion(habitacionDTO.getIdHabitacion());
        entity.setNumero(habitacionDTO.getNumero());
        entity.setPiso(habitacionDTO.getPiso());
        entity.setPrecio(habitacionDTO.getPrecio());
        entity.setCapacidad(habitacionDTO.getCapacidad());
        entity.setDetalles(habitacionDTO.getDetalles());

        if (habitacionDTO.getEstado() != null) {
            entity.setEstado(habitacionDTO.getEstado());
        }
        if (habitacionDTO.getTipoHabitacion() != null) {
            entity.setTipoHabitacion(habitacionDTO.getTipoHabitacion());
        }

        return entity;
    }

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
