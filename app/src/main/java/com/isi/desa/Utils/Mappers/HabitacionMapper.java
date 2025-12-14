package com.isi.desa.Utils.Mappers;

import com.isi.desa.Dto.Estadia.EstadiaDTO;
import com.isi.desa.Dto.Habitacion.HabitacionDTO;
import com.isi.desa.Model.Entities.Estadia.Estadia;
import com.isi.desa.Model.Entities.Habitacion.Habitacion;

import java.util.ArrayList;
import java.util.List;

public class HabitacionMapper {
    public static HabitacionDTO entityToDto(Habitacion h) {
        if (h == null) return null;
        HabitacionDTO dto = new HabitacionDTO();
        dto.idHabitacion = h.getIdHabitacion();
        dto.precio = h.getPrecio();
        dto.numero = h.getNumero();
        dto.piso = h.getPiso();
        dto.estado = h.getEstado();
        dto.capacidad = h.getCapacidad();
        dto.detalles = h.getDetalles();
        // El tipo de habitación se puede obtener por instanceof o discriminador
        if (h instanceof com.isi.desa.Model.Entities.Habitacion.DobleEstandar) {
            dto.tipoHabitacion = com.isi.desa.Model.Enums.TipoHabitacion.DOBLE_ESTANDAR;
        } else if (h instanceof com.isi.desa.Model.Entities.Habitacion.DobleSuperior) {
            dto.tipoHabitacion = com.isi.desa.Model.Enums.TipoHabitacion.DOBLE_SUPERIOR;
        } else if (h instanceof com.isi.desa.Model.Entities.Habitacion.IndividualEstandar) {
            dto.tipoHabitacion = com.isi.desa.Model.Enums.TipoHabitacion.INDIVIDUAL_ESTANDAR;
        } else if (h instanceof com.isi.desa.Model.Entities.Habitacion.SuiteDoble) {
            dto.tipoHabitacion = com.isi.desa.Model.Enums.TipoHabitacion.SUITE_DOBLE;
        } else if (h instanceof com.isi.desa.Model.Entities.Habitacion.SuperiorFamilyPlan) {
            dto.tipoHabitacion = com.isi.desa.Model.Enums.TipoHabitacion.SUPERIOR_FAMILY_PLAN;
        } else {
            dto.tipoHabitacion = null;
        }
        dto.cantidadCamasDobles = h.getCantidadCamasDobles();
        dto.cantidadCamasKingSize = h.getCantidadCamasKingSize();
        return dto;
    }

    public static Habitacion dtoToEntity(HabitacionDTO dto) {
        // Este método requiere lógica adicional para instanciar la subclase correcta según el tipoHabitacion
        if (dto == null || dto.tipoHabitacion == null) return null;
        Habitacion h;
        switch (dto.tipoHabitacion) {
            case DOBLE_ESTANDAR:
                h = new com.isi.desa.Model.Entities.Habitacion.DobleEstandar();
                break;
            case DOBLE_SUPERIOR:
                h = new com.isi.desa.Model.Entities.Habitacion.DobleSuperior();
                break;
            case INDIVIDUAL_ESTANDAR:
                h = new com.isi.desa.Model.Entities.Habitacion.IndividualEstandar();
                break;
            case SUITE_DOBLE:
                h = new com.isi.desa.Model.Entities.Habitacion.SuiteDoble();
                break;
            case SUPERIOR_FAMILY_PLAN:
                h = new com.isi.desa.Model.Entities.Habitacion.SuperiorFamilyPlan();
                break;
            default:
                return null;
        }
        h.setIdHabitacion(dto.idHabitacion);
        h.setPrecio(dto.precio);
        h.setNumero(dto.numero);
        h.setPiso(dto.piso);
        h.setEstado(dto.estado);
        h.setCapacidad(dto.capacidad);
        h.setDetalles(dto.detalles);
        h.setCantidadCamasDobles(dto.cantidadCamasDobles);
        h.setCantidadCamasKingSize(dto.cantidadCamasKingSize);
        return h;
    }

    public static List<Habitacion> dtoLisToEntitiesList(List<HabitacionDTO> dtos) {
        List<Habitacion> entities = new ArrayList<>();
        if (dtos != null) {
            for (HabitacionDTO dto : dtos) {
                Habitacion h = dtoToEntity(dto);
                if (h != null) entities.add(h);
            }
        }
        return entities;
    }
    public static List<HabitacionDTO> entityListToDtoList(List<Habitacion> habitaciones) {
        List<HabitacionDTO> dtoList = new ArrayList<>();
        if (habitaciones != null) {
            for (Habitacion h : habitaciones) {
                HabitacionDTO dto = entityToDto(h);
                if (dto != null) dtoList.add(dto);
            }
        }
        return dtoList;
    }
}
