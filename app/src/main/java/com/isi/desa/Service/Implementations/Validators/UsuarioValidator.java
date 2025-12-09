package com.isi.desa.Service.Implementations.Validators;

import com.isi.desa.Dao.Implementations.UsuarioDAO;
import com.isi.desa.Dao.Interfaces.IUsuarioDAO;
import com.isi.desa.Dto.Usuario.UsuarioDTO;
import com.isi.desa.Model.Entities.Usuario.Usuario;
import com.isi.desa.Service.Interfaces.Validators.IUsuarioValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UsuarioValidator implements IUsuarioValidator {
    @Autowired
    private IUsuarioDAO usuarioDAO;

    // Instancia unica (eager singleton)
    private static final UsuarioValidator INSTANCE = new UsuarioValidator();

    // Metodo publico para obtener la instancia
    public static UsuarioValidator getInstance() {
        return INSTANCE;
    }

    public Usuario create(UsuarioDTO usuarioDTO) {
        List<String> errores = validateCreate(usuarioDTO);
        if (errores != null && !errores.isEmpty()) {
            throw new IllegalArgumentException(String.join(", ", errores));
        }
        return new Usuario(usuarioDTO.idUsuario, usuarioDTO.contrasenia, usuarioDTO.nombre, usuarioDTO.apellido);
    }

    public List<String> validateCreate(UsuarioDTO usuarioDTO) {
        List<String> errores = new ArrayList<>();
        String error;
        error = validateIdUsuario(usuarioDTO.idUsuario); if (error != null) errores.add(error);
        error = validateContrasenia(usuarioDTO.contrasenia); if (error != null) errores.add(error);
        error = validateNombre(usuarioDTO.nombre); if (error != null) errores.add(error);
        error = validateApellido(usuarioDTO.apellido); if (error != null) errores.add(error);
        return errores.isEmpty() ? null : errores;
    }

    public String validateIdUsuario(String idUsuario) {
        if (idUsuario == null || idUsuario.trim().isEmpty()) return "El idUsuario es obligatorio";
        return null;
    }
    public String validateContrasenia(String contrasenia) {
        if (contrasenia == null || contrasenia.trim().isEmpty()) return "La contrasenia es obligatoria";
        if (contrasenia.length() < 6) return "La contrasenia debe tener al menos 6 caracteres";
        return null;
    }
    public String validateNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return "El nombre es obligatorio";
        return null;
    }
    public String validateApellido(String apellido) {
        if (apellido == null || apellido.trim().isEmpty()) return "El apellido es obligatorio";
        return null;
    }
}
