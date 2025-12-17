package com.isi.desa.Dao.Repositories;

import com.isi.desa.Model.Entities.Usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    // finder para login Spring Data lo implementa solo
    Optional<Usuario> findByNombreIgnoreCaseAndApellidoIgnoreCaseAndContrasenia(String nombre, String apellido, String contrasenia);
}
