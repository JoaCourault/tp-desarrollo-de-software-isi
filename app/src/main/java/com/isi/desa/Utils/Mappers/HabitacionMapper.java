package com.isi.desa.Utils.Mappers;

import com.isi.desa.Dto.Habitacion.HabitacionDTO;
import com.isi.desa.Model.Entities.Habitacion.*; // Importamos todas las hijas
import java.util.ArrayList;
import java.util.List;

public class HabitacionMapper {

    public static HabitacionDTO entityToDTO(HabitacionEntity e) {
        if (e == null) return null;
        HabitacionDTO dto = new HabitacionDTO();

        // 1. Campos Comunes
        dto.idHabitacion = e.getIdHabitacion();
        dto.precio = e.getPrecio();
        dto.numero = e.getNumero();
        dto.piso = e.getPiso();
        dto.capacidad = e.getCapacidad();
        dto.detalles = e.getDetalles();
        dto.estado = e.getEstado();

        // 2. Determinar Tipo y Campos Específicos (Polimorfismo inverso)
        // Usamos instanceof para saber qué tipo de objeto recuperó Hibernate
        if (e instanceof IndividualEstandar) {
            dto.tipoHabitacion = "Individual Estandar";
            dto.cantidadCamasIndividual = ((IndividualEstandar) e).getCantidadCamasIndividual();
        }
        else if (e instanceof SuiteDoble) {
            dto.tipoHabitacion = "Suite Doble";
            dto.cantidadCamasDobles = ((SuiteDoble) e).getCantidadCamasDobles();
        }
        else if (e instanceof DobleSuperior) {
            dto.tipoHabitacion = "Doble Superior";
            DobleSuperior ds = (DobleSuperior) e;
            dto.cantidadCamasIndividual = ds.getCantidadCamasIndividual();
            dto.cantidadCamasDobles = ds.getCantidadCamasDobles();
            dto.cantidadCamasKingSize = ds.getCantidadCamasKingSize();
        }
        else if (e instanceof SuperiorFamilyPlan) {
            dto.tipoHabitacion = "Superior Family Plan";
            SuperiorFamilyPlan sfp = (SuperiorFamilyPlan) e;
            dto.cantidadCamasIndividual = sfp.getCantidadCamasIndividual();
            dto.cantidadCamasDobles = sfp.getCantidadCamasDobles();
        }
        else if (e instanceof DobleEstandar) {
            dto.tipoHabitacion = "Doble Estandar";
            DobleEstandar de = (DobleEstandar) e;
            dto.cantidadCamasIndividual = de.getCantidadCamasIndividual();
            dto.cantidadCamasDobles = de.getCantidadCamasDobles();
        }

        return dto;
    }

    public static HabitacionEntity dtoToEntity(HabitacionDTO dto) {
        if (dto == null) return null;

        HabitacionEntity entity;

        // 1. Instanciar la clase hija correcta según el String
        if (dto.tipoHabitacion == null) {
            throw new RuntimeException("El tipo de habitación es obligatorio");
        }

        switch (dto.tipoHabitacion) {
            case "Individual Estandar":
                IndividualEstandar ind = new IndividualEstandar();
                ind.setCantidadCamasIndividual(dto.cantidadCamasIndividual);
                entity = ind;
                break;
            case "Suite Doble":
                SuiteDoble sd = new SuiteDoble();
                sd.setCantidadCamasDobles(dto.cantidadCamasDobles);
                entity = sd;
                break;
            case "Doble Superior":
                DobleSuperior ds = new DobleSuperior();
                ds.setCantidadCamasIndividual(dto.cantidadCamasIndividual);
                ds.setCantidadCamasDobles(dto.cantidadCamasDobles);
                ds.setCantidadCamasKingSize(dto.cantidadCamasKingSize);
                entity = ds;
                break;
            case "Superior Family Plan":
                SuperiorFamilyPlan sfp = new SuperiorFamilyPlan();
                sfp.setCantidadCamasIndividual(dto.cantidadCamasIndividual);
                sfp.setCantidadCamasDobles(dto.cantidadCamasDobles);
                entity = sfp;
                break;
            case "Doble Estandar":
                DobleEstandar de = new DobleEstandar();
                de.setCantidadCamasIndividual(dto.cantidadCamasIndividual);
                de.setCantidadCamasDobles(dto.cantidadCamasDobles);
                entity = de;
                break;
            default:
                throw new RuntimeException("Tipo de habitación desconocido: " + dto.tipoHabitacion);
        }

        // 2. Llenar Campos Comunes (La entidad ya fue instanciada arriba)
        entity.setIdHabitacion(dto.idHabitacion);
        entity.setPrecio(dto.precio);
        entity.setNumero(dto.numero);
        entity.setPiso(dto.piso);
        entity.setCapacidad(dto.capacidad);
        entity.setDetalles(dto.detalles);
        entity.setEstado(dto.estado);

        return entity;
    }

    public static List<HabitacionDTO> entityListToDtoList(List<HabitacionEntity> entities) {
        if (entities == null) return new ArrayList<>();
        List<HabitacionDTO> dtos = new ArrayList<>();
        for (HabitacionEntity h : entities) {
            dtos.add(entityToDTO(h));
        }
        return dtos;
    }
}