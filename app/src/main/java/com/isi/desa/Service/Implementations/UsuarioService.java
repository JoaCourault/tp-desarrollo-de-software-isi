package com.isi.desa.Service.Implementations;

import com.isi.desa.Dao.Implementations.UsuarioDAO;
import com.isi.desa.Dao.Interfaces.IUsuarioDAO;
import com.isi.desa.Dto.Resultado;
import com.isi.desa.Dto.Usuario.AutenticarUsuarioRequestDto;
import com.isi.desa.Dto.Usuario.AutenticarUsuarioResponseDto;
import com.isi.desa.Dto.Usuario.UsuarioDTO;
import com.isi.desa.Model.Entities.Usuario.Usuario;
import com.isi.desa.Service.Implementations.Validators.UsuarioValidator;
import com.isi.desa.Service.Interfaces.ILogger;
import com.isi.desa.Service.Interfaces.IUsuarioService;
import com.isi.desa.Service.Interfaces.Validators.IUsuarioValidator;
import com.isi.desa.Utils.Mappers.UsuarioMapper;

import java.util.Optional;

// @Service //Descomentar para correr con Spring Boot
public class UsuarioService implements IUsuarioService {
    //@Autowired //Descomentar para correr con Spring Boot
    private IUsuarioValidator validator;
    //@Autowired //Descomentar para correr con Spring Boot
    private ILogger logger;

    //@Autowired //Descomentar para correr con Spring Boot
    private IUsuarioDAO usuarioDAO;

    // Instancia unica (eager singleton)
    private static final UsuarioService INSTANCE = new UsuarioService();

    // Constructor privado para inyeccion de dependencias manual
    private UsuarioService() {
        this.validator = UsuarioValidator.getInstance();
        this.logger = Logger.getInstance();
        this.usuarioDAO = new UsuarioDAO();
    }

    // Metodo publico para obtener la instancia
    public static UsuarioService getInstance() {
        return INSTANCE;
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
        res.resultado.mensaje = "El usuario no existe. O la contrasenia incorrecta.";

        logger.info("Intento de login para: " + requestDto.nombre + " " + requestDto.apellido);

        Usuario u = this.usuarioDAO.login(
                requestDto.nombre,
                requestDto.apellido,
                requestDto.password
        );
        if (u != null) {
            res.resultado.id = 0;
            res.resultado.mensaje = "Login exitoso.";
            res.usuario = UsuarioMapper.entityToDTO(u);
            logger.info("Login exitoso para: " + u.getNombre() + " " + u.getApellido());
        } else {
            logger.warn("Login fallido para: " + requestDto.nombre + " " + requestDto.apellido);
        }
        return res;
    }
}
