package com.isi.desa.Utils.Mappers;

import com.isi.desa.Dto.Direccion.DireccionDTO;
import com.isi.desa.Model.Entities.Direccion.Direccion;

public class DireccionMapper {

    // --- NOMBRE CORREGIDO: DTO en mayúsculas ---
    public static DireccionDTO entityToDTO(Direccion d) {
        if (d == null) return null;
        DireccionDTO dto = new DireccionDTO();
        dto.id= d.getIdDireccion();
        dto.pais = d.getPais();
        dto.provincia = d.getProvincia();
        dto.localidad = d.getLocalidad();
        dto.codigoPostal = d.getCp();
        dto.calle = d.getCalle();
        dto.numero = d.getNumero();
        dto.departamento = d.getDepartamento();
        dto.piso = d.getPiso();
        return dto;
    }
    public static Direccion dtoToEntity(DireccionDTO dto) {
        if (dto == null) return null;
        Direccion d = new Direccion();
        d.setIdDireccion(dto.id);
        d.setPais(dto.pais);
        d.setProvincia(dto.provincia);
        d.setLocalidad(dto.localidad);
        d.setCp(dto.codigoPostal);
        d.setCalle(dto.calle);
        d.setNumero(dto.numero);
        d.setDepartamento(dto.departamento);
        d.setPiso(dto.piso);
        return d;
    }
}