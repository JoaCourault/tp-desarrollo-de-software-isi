package com.isi.desa;

import com.isi.desa.Dao.Implementations.UsuarioDAO;
import com.isi.desa.Dto.Usuario.UsuarioDTO;
import com.isi.desa.Model.Entities.Usuario.Usuario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UsuarioDAOTest {

    @Test
    void testCrearUsuario() {
        UsuarioDAO dao = new UsuarioDAO();

        UsuarioDTO dto = new UsuarioDTO();
        dto.idUsuario = "tester";
        dto.contrasenia = "clave123";
        dto.nombre = "Agustin";
        dto.apellido = "Prueba";

        try {
            Usuario creado = dao.crear(dto);
            assertNotNull(creado);
            assertEquals("tester", creado.getIdUsuario());
            System.out.println("✅ Usuario creado correctamente: " + creado.getIdUsuario());
        } catch (RuntimeException e) {
            fail("❌ Error al crear usuario: " + e.getMessage());
        }
    }

    @Test
    void testModificarUsuario() {
        UsuarioDAO dao = new UsuarioDAO();

        UsuarioDTO dto = new UsuarioDTO();
        dto.idUsuario = "tester";
        dto.contrasenia = "claveNueva456";
        dto.nombre = "Agus";
        dto.apellido = "Actualizado";

        try {
            Usuario actualizado = dao.modificar(dto);
            assertNotNull(actualizado);
            assertEquals("claveNueva456", actualizado.getContrasenia());
            System.out.println("✅ Usuario modificado correctamente: " + actualizado.getIdUsuario());
        } catch (RuntimeException e) {
            fail("❌ Error al modificar usuario: " + e.getMessage());
        }
    }

    @Test
    void testObtenerUsuario() {
        UsuarioDAO dao = new UsuarioDAO();

        try {
            Usuario usuario = dao.obtener("tp-desarrollo");
            assertNotNull(usuario);
            assertEquals("tp-desarrollo", usuario.getIdUsuario());
            System.out.println("✅ Usuario obtenido: " + usuario.getNombre() + " " + usuario.getApellido());
        } catch (RuntimeException e) {
            fail("❌ Error al obtener usuario: " + e.getMessage());
        }
    }

    @Test
    void testEliminarUsuario() {
        UsuarioDAO dao = new UsuarioDAO();

        UsuarioDTO dto = new UsuarioDTO();
        dto.idUsuario = "tester";

        try {
            Usuario eliminado = dao.eliminar(dto);
            assertNotNull(eliminado);
            System.out.println("✅ Usuario eliminado correctamente: " + eliminado.getIdUsuario());
        } catch (RuntimeException e) {
            fail("❌ Error al eliminar usuario: " + e.getMessage());
        }
    }
}
