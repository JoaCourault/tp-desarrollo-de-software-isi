package com.isi.desa.Service.Implementations.Validators;

import com.isi.desa.Dto.Usuario.UsuarioDTO;
import com.isi.desa.Model.Entities.Usuario.Usuario;
import com.isi.desa.Dao.Interfaces.IUsuarioDAO;
import com.isi.desa.Service.Implementations.Validators.ReservaValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UsuarioValidatorTest {

    @InjectMocks
    private UsuarioValidator validator;

    @Mock
    private IUsuarioDAO usuarioDAO;

    /* =========================
       validateCreate
       ========================= */

    @Test
    void validateCreate_dtoValido_retornaNull() {
        UsuarioDTO dto = crearDtoValido();

        List<String> errores = validator.validateCreate(dto);

        assertNull(errores);
    }

    @Test
    void validateCreate_idVacio_error() {
        UsuarioDTO dto = crearDtoValido();
        dto.idUsuario = "";

        List<String> errores = validator.validateCreate(dto);

        assertNotNull(errores);
        assertTrue(errores.contains("El idUsuario es obligatorio"));
    }

    @Test
    void validateCreate_nombreVacio_error() {
        UsuarioDTO dto = crearDtoValido();
        dto.nombre = "";

        List<String> errores = validator.validateCreate(dto);

        assertNotNull(errores);
        assertTrue(errores.contains("El nombre es obligatorio"));
    }

    @Test
    void validateCreate_apellidoVacio_error() {
        UsuarioDTO dto = crearDtoValido();
        dto.apellido = "";

        List<String> errores = validator.validateCreate(dto);

        assertNotNull(errores);
        assertTrue(errores.contains("El apellido es obligatorio"));
    }

    /* =========================
       validateContrasenia
       ========================= */

    @Test
    void validateContrasenia_menosDe5Letras_error() {
        String error = validator.validateContrasenia("123ab");

        assertNotNull(error);
        assertTrue(error.contains("5 letras"));
    }

    @Test
    void validateContrasenia_menosDe3Numeros_error() {
        String error = validator.validateContrasenia("abcdef1");

        assertNotNull(error);
        assertTrue(error.contains("3 números"));
    }

    @Test
    void validateContrasenia_numerosIguales_error() {
        String error = validator.validateContrasenia("abcde112");

        assertNotNull(error);
        assertTrue(error.contains("iguales"));
    }

    @Test
    void validateContrasenia_numerosConsecutivos_error() {
        String error = validator.validateContrasenia("abcde123");

        assertNotNull(error);
        assertTrue(error.contains("consecutivos"));
    }

    @Test
    void validateContrasenia_valida_ok() {
        String error = validator.validateContrasenia("abcde135");

        assertNull(error);
    }

    /* =========================
       create
       ========================= */

    @Test
    void create_datosValidos_creaUsuario() {
        UsuarioDTO dto = crearDtoValido();

        Usuario usuario = validator.create(dto);

        assertNotNull(usuario);
        assertEquals(dto.idUsuario, usuario.getIdUsuario());
        assertEquals(dto.nombre, usuario.getNombre());
        assertEquals(dto.apellido, usuario.getApellido());
    }

    @Test
    void create_datosInvalidos_lanzaException() {
        UsuarioDTO dto = crearDtoValido();
        dto.contrasenia = "123"; // inválida

        assertThrows(IllegalArgumentException.class, () -> validator.create(dto));
    }

    /* =========================
       helpers
       ========================= */

    private UsuarioDTO crearDtoValido() {
        UsuarioDTO dto = new UsuarioDTO();
        dto.idUsuario = "user1";
        dto.contrasenia = "abcde135";
        dto.nombre = "Juan";
        dto.apellido = "Perez";
        return dto;
    }
}
