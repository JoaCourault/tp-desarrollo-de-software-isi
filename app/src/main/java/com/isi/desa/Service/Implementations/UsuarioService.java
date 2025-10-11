package com.isi.desa.Service.Implementations;

import com.isi.desa.Dao.Implementations.UsuarioDAO;
import com.isi.desa.Dao.Interfaces.IUsuarioDAO;
import com.isi.desa.Dto.Usuario.AutenticarUsuarioRequestDto;
import com.isi.desa.Dto.Usuario.AutenticarUsuarioResponseDto;
import com.isi.desa.Dto.Usuario.UsuarioDTO;
import com.isi.desa.Model.Entities.Usuario.Usuario;
import com.isi.desa.Service.Implementations.Validators.UsuarioValidator;
import com.isi.desa.Service.Interfaces.ILogger;
import com.isi.desa.Service.Interfaces.IUsuarioService;
import com.isi.desa.Service.Interfaces.Validators.IUsuarioValidator;

import java.util.Optional;

//@Service //Descomentar para correr con Spring Boot
public class UsuarioService implements IUsuarioService {
    //@Autowired //Descomentar para correr con Spring Boot
    private IUsuarioValidator validator;
    //@Autowired //Descomentar para correr con Spring Boot
    private ILogger logger;

    //@Autowired //Descomentar para correr con Spring Boot
    private IUsuarioDAO usuarioDAO;

    // Constructor para inyeccion de dependencias manual (sin Spring Boot, borrar cuando se use Spring)
    public UsuarioService() {
        this.validator = new UsuarioValidator();
        this.logger = new Logger();
        this.usuarioDAO = new UsuarioDAO();
    }

    @Override
    public Optional<Usuario> crearUsuario(UsuarioDTO usuarioDTO) {
        try {
            Usuario newUser = validator.create(usuarioDTO);
            return Optional.of(newUser); // Retornar el usuario creado envuelto en Optional.
        } catch (Exception e) {
            throw e; // Propaga la excepcion para que el controlador la maneje.
        }
    }

    @Override
    public AutenticarUsuarioResponseDto login(AutenticarUsuarioRequestDto requestDto) {
        AutenticarUsuarioResponseDto res = new AutenticarUsuarioResponseDto();
        res.resultado.id = 2; // Por defecto, no encontrado (404)
        res.resultado.mensaje = "El usuario no existe. O la contraseña incorrecta.";
        Usuario u = this.usuarioDAO.login(
                requestDto.nombre,
                requestDto.apellido,
                requestDto.password
        );
        if (u != null) {
            res.resultado.id = 0;
            res.resultado.mensaje = "Login exitoso.";
        }
        return res;
    }
}
