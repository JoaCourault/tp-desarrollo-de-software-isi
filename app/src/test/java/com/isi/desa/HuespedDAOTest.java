package com.isi.desa;

import com.isi.desa.Dao.Implementations.HuespedDAO;
import com.isi.desa.Dto.Direccion.DireccionDTO;
import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Dto.TipoDocumento.TipoDocumentoDTO;
import com.isi.desa.Exceptions.HuespedDuplicadoException;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class HuespedDAOTest {

    private HuespedDTO buildNuevoHuesped(String id, String dni) {
        HuespedDTO nuevo = new HuespedDTO();
        nuevo.idHuesped = id;                    // p.ej. "HU-999"
        nuevo.nombre = "aGUS";
        nuevo.apellido = "ACOSTA";

        // TipoDocumentoDTO (NO string)
        TipoDocumentoDTO td = new TipoDocumentoDTO();
        td.tipoDocumento = "TD-01";
        td.descripcion = "DNI";
        nuevo.tipoDocumento = td;

        nuevo.numDoc = dni;                      // p.ej. "99999999"
        nuevo.posicionIva = "Consumidor Final";
        nuevo.cuit = "20-45946104-9";
        nuevo.fechaNacimiento = LocalDate.of(1999, 1, 1);
        nuevo.telefono = "+54 9 11 9999-9999";
        nuevo.email = "test.junit@example.com";
        nuevo.ocupacion = "Tester";
        nuevo.nacionalidad = "Argentina";

        // Dirección: si vas a referenciar una existente, con setear el id alcanza
        DireccionDTO dir = new DireccionDTO();
        dir.id = "DI-002";
        nuevo.direccion = dir;

        return nuevo;
    }

    @Test
    void testCrearHuesped() {
        HuespedDAO dao = new HuespedDAO();

        HuespedDTO nuevo = buildNuevoHuesped("HU-104", "45946104");

        try {
            Huesped creado = dao.crear(nuevo);
            assertNotNull(creado, "El huesped creado no debe ser nulo");
            assertEquals("HU-104", creado.getIdHuesped(), "El ID deberia coincidir");
            assertEquals("45946104", creado.getNumDoc(), "El DNI deberia coincidir");
            System.out.println("✓ Huesped creado: " + creado.getNombre() + " (" + creado.getIdHuesped() + ")");
        } catch (HuespedDuplicadoException e) {
            fail("No deberia lanzarse HuespedDuplicadoException en alta válida");
        } catch (Exception e) {
            fail("Error inesperado al crear huesped: " + e.getMessage());
        }
    }

    @Test
    void testObtenerHuesped() {
        HuespedDAO dao = new HuespedDAO();
        // Usar un DNI que sí existe en tu JSON
        String dniExistente = "45946104";

        try {
            Huesped h = dao.obtenerHuesped(dniExistente);
            assertNotNull(h, "El huesped obtenido no debe ser nulo");
            assertEquals(dniExistente, h.getNumDoc());
            System.out.println("✓ Huesped obtenido: " + h.getNombre() + " " + h.getApellido());
        } catch (RuntimeException e) {
            fail("Error al obtener huesped: " + e.getMessage());
        }
    }

    @Test
    void testEliminarHuesped() {
        HuespedDAO dao = new HuespedDAO();

        final String idAEliminar = "HU-104";
        final String dniAEliminar = "45946104";

        try {
            boolean debeCrear = true;
            try {
                dao.obtenerHuesped(dniAEliminar);
                debeCrear = false;
            } catch (Exception ignore) { /* no existe → lo creamos */ }

            if (debeCrear) {
                try {
                    dao.crear(buildNuevoHuesped(idAEliminar, dniAEliminar));
                } catch (HuespedDuplicadoException e) {
                    fail("No debería lanzar duplicado al preparar datos de prueba");
                }
            }

            Huesped eliminado = dao.eliminar(idAEliminar);
            assertNotNull(eliminado, "El huesped eliminado no debe ser nulo");
            assertEquals(idAEliminar, eliminado.getIdHuesped(), "El id eliminado deberia coincidir");
            System.out.println("✓ Huesped marcado eliminado: " + eliminado.getIdHuesped());

        } catch (RuntimeException e) {
            fail("Error al eliminar huesped: " + e.getMessage());
        }
    }

}
