package com.isi.desa.Utils.Mappers;

import com.isi.desa.Dto.Habitacion.HabitacionDTO;
import com.isi.desa.Model.Entities.Habitacion.*; // Importar todas las hijas

public class HabitacionMapper {

    public static HabitacionDTO entityToDTO(Habitacion entity) {
        if (entity == null) return null;
        HabitacionDTO dto = new HabitacionDTO();
        dto.idHabitacion = entity.getIdHabitacion();
        dto.precio = entity.getPrecio();
        dto.numero = entity.getNumero();
        dto.piso = entity.getPiso();
        dto.capacidad = entity.getCapacidad();
        dto.detalles = entity.getDetalles();
        dto.estado = entity.getEstado();
        dto.qCamDobles = entity.getCantidadCamasDobles();
        dto.qCamIndividual = entity.getCantidadCamasIndividual();
        dto.qCamKingSize = entity.getCantidadCamasKingSize();
        dto.tipoHabitacion = entity.getTipoHabitacionString();

        return dto;
    }

    public static Habitacion dtoToEntity(HabitacionDTO dto) {
        if (dto == null) return null;

        Habitacion entity;

        // FÁBRICA DE INSTANCIAS: Dependiendo del String, creamos el HIJO concreto
        if (dto.tipoHabitacion == null) {
            throw new RuntimeException("El tipo de habitación es obligatorio para crear la entidad.");
        }

        switch (dto.tipoHabitacion.toUpperCase()) {
            case "DOBLE_ESTANDAR": entity = new DobleEstandar(); break;
            case "DOBLE_SUPERIOR": entity = new DobleSuperior(); break;
            case "INDIVIDUAL_ESTANDAR": entity = new IndividualEstandar(); break;
            case "SUITE_DOBLE": entity = new SuiteDoble(); break;
            case "SUPERIOR_FAMILY_PLAN": entity = new SuperiorFamilyPlan(); break;
            default: throw new RuntimeException("Tipo de habitación desconocido: " + dto.tipoHabitacion);
        }

        // Asignamos propiedades comunes (definidas en la clase abstracta)
        entity.setIdHabitacion(dto.idHabitacion);
        entity.setPrecio(dto.precio);
        entity.setNumero(dto.numero);
        entity.setPiso(dto.piso);
        entity.setCapacidad(dto.capacidad);
        entity.setDetalles(dto.detalles);
        entity.setEstado(dto.estado);
        entity.setCantidadCamasDobles(dto.qCamDobles);
        entity.setCantidadCamasIndividual(dto.qCamIndividual);
        entity.setCantidadCamasKingSize(dto.qCamKingSize);

        return entity;
    }
}