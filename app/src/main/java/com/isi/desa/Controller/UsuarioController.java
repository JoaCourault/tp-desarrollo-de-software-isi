package com.isi.desa.Controller;

import com.isi.desa.Dto.UsuarioDTO;
import com.isi.desa.Model.Entities.Usuario.Usuario;
import com.isi.desa.Service.Implementations.UsuarioService;
import com.isi.desa.Service.Implementations.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import java.util.Optional;

//@Controller//Descomentar para correr con Spring Boot
public class UsuarioController {
    //@Autowired //Descomentar para correr con Spring Boot
    private UsuarioService service;
    //@Autowired //Descomentar para correr con Spring Boot
    private Logger logger;


    // Constructor para inyección de dependencias manual (sin Spring Boot, borrar cuando se use Spring)
    public UsuarioController() {
        this.service = new  UsuarioService();
        this.logger = new  Logger();
    }

    public void crearUsuario (UsuarioDTO usuarioDTO) {
        try {
            Optional<Usuario> u = this.service.crearUsuario(usuarioDTO);
            if (u.isPresent()) logger.info("Usuario creado con exito.");
            else logger.error("No se pudo crear el usuario.", null);
        } catch (Exception e) {
            logger.error("Error en el controlador al crear el usuario: " + e.getMessage(), e);
        }
    }
}
