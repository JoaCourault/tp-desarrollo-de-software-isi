package com.isi.desa.Utils.Mappers;

import com.isi.desa.Model.Entities.Usuario.Usuario;
import com.isi.desa.Dto.Usuario.UsuarioDTO;

public class UsuarioMapper {
    public static UsuarioDTO entityToDTO(Usuario u) {
        if (u == null) return null;
        UsuarioDTO dto = new UsuarioDTO();
        dto.idUsuario = u.getIdUsuario();
        dto.contrasenia = u.getContrasenia();
        dto.nombre = u.getNombre();
        dto.apellido = u.getApellido();
        return dto;
    }

    public static Usuario dtoToEntity(UsuarioDTO dto) {
        if (dto == null) return null;
        return new Usuario(dto.idUsuario, dto.contrasenia, dto.nombre, dto.apellido);
    }
}

