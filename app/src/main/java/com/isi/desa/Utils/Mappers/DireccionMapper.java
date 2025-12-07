package com.isi.desa.Utils.Mappers;

import com.isi.desa.Dto.Direccion.DireccionDTO;
import com.isi.desa.Model.Entities.Direccion.Direccion;

public class DireccionMapper {

    public static DireccionDTO entityToDto(Direccion d) {
        if (d == null) return null;
        DireccionDTO dto = new DireccionDTO();
        dto.setId(d.getIdDireccion());
        dto.setPais(d.getPais());
        dto.setProvincia(d.getProvincia());
        dto.setLocalidad(d.getLocalidad());

        // Mapeo inverso: Entidad (cp) -> DTO (codigoPostal)
        dto.setCodigoPostal(d.getCp());

        dto.setCalle(d.getCalle());
        dto.setNumero(d.getNumero());
        dto.setDepartamento(d.getDepartamento());

        // Manejo de piso (Integer a String)
        if (d.getPiso() != null) {
            dto.setPiso(String.valueOf(d.getPiso()));
        }
        return dto;
    }

    public static Direccion dtoToEntity(DireccionDTO dto) {
        if (dto == null) return null;
        Direccion d = new Direccion();
        d.setIdDireccion(dto.getId());
        d.setPais(dto.getPais());
        d.setProvincia(dto.getProvincia());
        d.setLocalidad(dto.getLocalidad());

        // Mapeo clave: DTO (codigoPostal) -> Entidad (cp)
        d.setCp(dto.getCodigoPostal());

        d.setCalle(dto.getCalle());
        d.setNumero(dto.getNumero());
        d.setDepartamento(dto.getDepartamento());

        // Manejo de piso (String a Integer seguro)
        if (dto.getPiso() != null && !dto.getPiso().isBlank() && dto.getPiso().matches("\\d+")) {
            d.setPiso(Integer.parseInt(dto.getPiso()));
        }
        return d;
    }
}