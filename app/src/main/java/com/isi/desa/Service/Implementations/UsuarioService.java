package com.isi.desa.Service.Implementations;

import com.isi.desa.Dto.UsuarioDTO;
import com.isi.desa.Model.Entities.Usuario.Usuario;
import com.isi.desa.Service.Implementations.Validators.UsuarioValidator;
import com.isi.desa.Service.Interfaces.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

//@Service //Descomentar para correr con Spring Boot
public class UsuarioService implements IUsuarioService {
    //@Autowired //Descomentar para correr con Spring Boot
    private UsuarioValidator validator;
    //@Autowired //Descomentar para correr con Spring Boot
    private Logger logger;

    // Constructor para inyección de dependencias manual (sin Spring Boot, borrar cuando se use Spring)
    public UsuarioService() {
        this.validator = new UsuarioValidator();
        this.logger = new Logger();
    }

    @Override
    public Optional<Usuario> crearUsuario(UsuarioDTO usuarioDTO) {
        try {
            Usuario newUser = validator.create(usuarioDTO);
            return Optional.of(newUser); // Retornar el usuario creado envuelto en Optional.
        } catch (Exception e) {
            throw e; // Propagar la excepción para que el controlador la maneje.
        }
    }
}
