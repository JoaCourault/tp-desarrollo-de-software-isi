package com.isi.desa.Service.Interfaces.Validators;

import com.isi.desa.Dto.Usuario.UsuarioDTO;
import com.isi.desa.Model.Entities.Usuario.Usuario;

import java.util.List;

public interface IUsuarioValidator {
    public Usuario create(UsuarioDTO usuarioDTO);
    public List<String> validateCreate(UsuarioDTO usuarioDTO);
    public String validateIdUsuario(String idUsuario);
    public String validateContrasenia(String contrasenia);
    public String validateNombre(String nombre);
    public String validateApellido(String apellido);
}
