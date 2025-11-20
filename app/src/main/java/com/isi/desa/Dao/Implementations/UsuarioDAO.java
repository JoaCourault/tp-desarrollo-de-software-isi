package com.isi.desa.Dao.Implementations;

import com.isi.desa.Dao.Interfaces.IUsuarioDAO;
import com.isi.desa.Dao.Repositories.UsuarioRepository;
import com.isi.desa.Model.Entities.Usuario.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UsuarioDAO implements IUsuarioDAO {
    @Autowired
    private UsuarioRepository repository;

    @Transactional(readOnly = true)
    public Usuario login(String nombre, String apellido, String contrasenia) {
        return repository.findByNombreIgnoreCaseAndApellidoIgnoreCaseAndContrasenia(nombre, apellido, contrasenia)
                .orElse(null);
    }
}
