package com.isi.desa.Utils.Mappers;

import com.isi.desa.Dto.Direccion.DireccionDTO;
import com.isi.desa.Model.Entities.Direccion.Direccion;

public class DireccionMapper {
    public static DireccionDTO entityToDto(Direccion d) {
        DireccionDTO dto = new DireccionDTO();
        dto.idDireccion= d.getIdDireccion();
        dto.pais = d.getPais();
        dto.provincia = d.getProvincia();
        dto.localidad = d.getLocalidad();
        dto.cp = d.getCp();
        dto.calle = d.getCalle();
        dto.numero = d.getNumero();
        dto.departamento = d.getDepartamento();
        dto.piso = d.getPiso();
        return dto;
    }
    public static Direccion dtoToEntity(DireccionDTO dto) {
        Direccion d = new Direccion();
        d.setIdDireccion(dto.idDireccion);
        d.setPais(dto.pais);
        d.setProvincia(dto.provincia);
        d.setLocalidad(dto.localidad);
        d.setCp(dto.cp);
        d.setCalle(dto.calle);
        d.setNumero(dto.numero);
        d.setDepartamento(dto.departamento);
        d.setPiso(dto.piso);
        return d;
    }
}