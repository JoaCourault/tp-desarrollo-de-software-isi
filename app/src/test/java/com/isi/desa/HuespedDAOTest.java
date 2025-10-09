package com.isi.desa;

import com.isi.desa.Dao.Implementations.HuespedDAO;
import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HuespedDAOTest {

    @Test
    void testCrearHuesped() {
        HuespedDAO huespedDAO = new HuespedDAO();

        // ✅ Creamos un nuevo huésped DTO
        HuespedDTO nuevo = new HuespedDTO();
        nuevo.idHuesped = "HU-999";
        nuevo.nombre = "Test";
        nuevo.apellido = "JUnit";
        nuevo.tipoDocumento = "TD-01";
        nuevo.numDoc = "99999999";
        nuevo.posicionIva = "Consumidor Final";
        nuevo.cuit = "20999999999";
        nuevo.fechaNacimiento = java.time.LocalDate.of(1999, 1, 1);
        nuevo.telefono = "+54 9 11 9999-9999";
        nuevo.email = "test.junit@example.com";
        nuevo.ocupacion = "Tester";
        nuevo.nacionalidad = "Argentina";

        // 👉 IMPORTANTE: el campo direccion es un ID referenciado
        com.isi.desa.Dto.Direccion.DireccionDTO dir = new com.isi.desa.Dto.Direccion.DireccionDTO();
        dir.id = "DI-001";
        nuevo.direccion = dir;

        try {
            Huesped creado = huespedDAO.crear(nuevo);
            assertNotNull(creado, "El huésped creado no debe ser nulo");
            assertEquals("HU-999", creado.getIdHuesped(), "El ID debería coincidir");
            System.out.println("✅ Huésped creado correctamente: " + creado.getNombre() + " (" + creado.getIdHuesped() + ")");
        } catch (RuntimeException e) {
            fail("❌ Error al crear huésped: " + e.getMessage());
        }
    }

    @Test
    void testObtenerHuesped() {
        HuespedDAO huespedDAO = new HuespedDAO();

        try {
            Huesped h = huespedDAO.obtenerHuesped("35648972"); // un DNI existente del JSON
            assertNotNull(h, "El huésped obtenido no debe ser nulo");
            System.out.println("✅ Huésped obtenido correctamente: " + h.getNombre() + " " + h.getApellido());
        } catch (RuntimeException e) {
            fail("❌ Error al obtener huésped: " + e.getMessage());
        }
    }

    @Test
    void testEliminarHuesped() {
        HuespedDAO huespedDAO = new HuespedDAO();

        HuespedDTO eliminar = new HuespedDTO();
        eliminar.numDoc = "99999999"; // el mismo que se creó en el primer test

        try {
            Huesped eliminado = huespedDAO.eliminar(eliminar);
            assertNotNull(eliminado, "El huésped eliminado no debe ser nulo");
            assertEquals("99999999", eliminado.getNumDoc(), "El DNI eliminado debería coincidir");
            System.out.println("✅ Huésped eliminado correctamente: " + eliminado.getNombre());
        } catch (RuntimeException e) {
            fail("❌ Error al eliminar huésped: " + e.getMessage());
        }
    }
}
