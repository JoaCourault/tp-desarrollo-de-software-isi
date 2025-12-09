// java
package com.isi.desa.Controller;

import com.isi.desa.Dto.Usuario.AutenticarUsuarioRequestDto;
import com.isi.desa.Dto.Usuario.AutenticarUsuarioResponseDto;
import com.isi.desa.Dto.Usuario.UsuarioDTO;
import com.isi.desa.Model.Entities.Usuario.Usuario;
import com.isi.desa.Service.Interfaces.ILogger;
import com.isi.desa.Service.Interfaces.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/Usuario")
public class UsuarioController {
    @Autowired
    private IUsuarioService service;
    @Autowired
    private ILogger logger;

    @PostMapping
    public ResponseEntity<Void> crearUsuario(@RequestBody UsuarioDTO usuarioDTO) {
        try {
            Optional<Usuario> u = this.service.crearUsuario(usuarioDTO);
            if (u.isPresent()) {
                logger.info("Usuario creado con exito.");
                return ResponseEntity.status(HttpStatus.CREATED).build();
            } else {
                logger.error("No se pudo crear el usuario.", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } catch (Exception e) {
            logger.error("Error al crear el usuario: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/Login")
    public ResponseEntity<AutenticarUsuarioResponseDto> autenticarUsuario(@RequestBody AutenticarUsuarioRequestDto requestDto) {
        try {
            AutenticarUsuarioResponseDto res = this.service.login(requestDto);
            if (res != null) {
                return ResponseEntity.ok(res);
            } else {
                logger.info("Credenciales invalidas.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            logger.error("Error al hacer login: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}