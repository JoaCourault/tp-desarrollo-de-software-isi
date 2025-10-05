package com.isi.desa.Service.Interfaces;

import com.isi.desa.Dto.Usuario.UsuarioDTO;
import com.isi.desa.Model.Entities.Usuario.Usuario;

import java.util.Optional;

public interface IUsuarioService {
    Optional<Usuario> crearUsuario(UsuarioDTO usuarioDTO);
}
