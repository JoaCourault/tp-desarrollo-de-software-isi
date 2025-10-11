package com.isi.desa.Controller;

import com.isi.desa.Dto.Usuario.AutenticarUsuarioRequestDto;
import com.isi.desa.Dto.Usuario.AutenticarUsuarioResponseDto;
import com.isi.desa.Dto.Usuario.UsuarioDTO;
import com.isi.desa.Model.Entities.Usuario.Usuario;
import com.isi.desa.Service.Implementations.UsuarioService;
import com.isi.desa.Service.Implementations.Logger;
import com.isi.desa.Service.Interfaces.ILogger;
import com.isi.desa.Service.Interfaces.IUsuarioService;

import java.util.Optional;

//@Controller//Descomentar para correr con Spring Boot
public class UsuarioController {
    //@Autowired //Descomentar para correr con Spring Boot
    private IUsuarioService service;
    //@Autowired //Descomentar para correr con Spring Boot
    private ILogger logger;


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
            logger.error("Error al crear el usuario: " + e.getMessage(), e);
        }
    }

    public AutenticarUsuarioResponseDto autenticarUsuario(AutenticarUsuarioRequestDto requestDto) {
        try {
            AutenticarUsuarioResponseDto res = this.service.login(requestDto);
            return res;
        } catch (Exception e) {
            logger.error("Error al hacer login: " + e.getMessage(), e);
            return null;
        }
    }
}
