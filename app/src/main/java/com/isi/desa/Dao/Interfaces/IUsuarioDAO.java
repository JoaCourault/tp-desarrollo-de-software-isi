package com.isi.desa.Dao.Interfaces;

import com.isi.desa.Dto.Usuario.UsuarioDTO;
import com.isi.desa.Model.Entities.Usuario.Usuario;

public interface IUsuarioDAO {
    Usuario crear(UsuarioDTO dto);
    Usuario modificar(UsuarioDTO dto);
    Usuario obtener(String idUsuario);
    Usuario eliminar(UsuarioDTO dto);
}
