package com.isi.desa.Utils.Mappers;

import com.isi.desa.Dto.Direccion.DireccionDTO;
import com.isi.desa.Model.Entities.Direccion.Direccion;

public class DireccionMapper {

    public static DireccionDTO entityToDto(Direccion d) {
        if (d == null) return null;

        DireccionDTO dto = new DireccionDTO();
        dto.id = d.getIdDireccion();
        dto.calle = d.getCalle();
        dto.numero = d.getNumero();
        dto.departamento = d.getDepartamento();
        dto.piso = d.getPiso();
        dto.cp = d.getCp();
        dto.localidad = d.getLocalidad();
        dto.provincia = d.getProvincia();
        dto.pais = d.getPais();
        return dto;
    }

    public static Direccion dtoToEntity(DireccionDTO dto) {
        if (dto == null) return null;

        Direccion d = new Direccion();
        d.setIdDireccion(dto.id);
        d.setCalle(dto.calle);
        d.setNumero(dto.numero);
        d.setDepartamento(dto.departamento);
        d.setPiso(dto.piso);
        d.setCp(dto.cp);
        d.setLocalidad(dto.localidad);
        d.setProvincia(dto.provincia);
        d.setPais(dto.pais);
        return d;
    }
}
