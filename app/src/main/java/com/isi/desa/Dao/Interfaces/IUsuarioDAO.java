package com.isi.desa.Dao.Interfaces;

import com.isi.desa.Model.Entities.Usuario.Usuario;
import org.springframework.stereotype.Service;

@Service
public interface IUsuarioDAO {
    Usuario login(String nombre, String apellido, String contrasenia);
}
