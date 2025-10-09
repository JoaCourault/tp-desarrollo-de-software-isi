package com.isi.desa;

import com.isi.desa.Dao.Implementations.TipoDocumentoDAO;
import com.isi.desa.Dto.TipoDocumento.TipoDocumentoDTO;
import com.isi.desa.Model.Entities.Tipodocumento.TipoDocumento;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TipoDocumentoDAOTest {

    @Test
    void testCrearTipoDocumento() {
        TipoDocumentoDAO dao = new TipoDocumentoDAO();

        TipoDocumentoDTO dto = new TipoDocumentoDTO();
        dto.tipoDocumento = "TD-99";
        dto.descripcion = "Prueba Temporal";

        try {
            TipoDocumento creado = dao.crear(dto);
            assertNotNull(creado);
            assertEquals("TD-99", creado.getTipoDocumento());
            System.out.println("✅ Creado correctamente: " + creado.getTipoDocumento() + " - " + creado.getDescripcion());
        } catch (RuntimeException e) {
            fail("❌ Error al crear: " + e.getMessage());
        }
    }

    @Test
    void testModificarTipoDocumento() {
        TipoDocumentoDAO dao = new TipoDocumentoDAO();

        TipoDocumentoDTO dto = new TipoDocumentoDTO();
        dto.tipoDocumento = "TD-99"; // ya debe existir por el test anterior
        dto.descripcion = "Descripción actualizada";

        try {
            TipoDocumento modificado = dao.modificar(dto);
            assertNotNull(modificado);
            assertEquals("Descripción actualizada", modificado.getDescripcion());
            System.out.println("✅ Modificado correctamente: " + modificado.getTipoDocumento() + " - " + modificado.getDescripcion());
        } catch (RuntimeException e) {
            fail("❌ Error al modificar: " + e.getMessage());
        }
    }

    @Test
    void testObtenerTipoDocumento() {
        TipoDocumentoDAO dao = new TipoDocumentoDAO();

        try {
            TipoDocumento tipo = dao.obtener("TD-01");
            assertNotNull(tipo);
            assertEquals("TD-01", tipo.getTipoDocumento());
            System.out.println("✅ Obtenido correctamente: " + tipo.getTipoDocumento() + " - " + tipo.getDescripcion());
        } catch (RuntimeException e) {
            fail("❌ Error al obtener: " + e.getMessage());
        }
    }

    @Test
    void testEliminarTipoDocumento() {
        TipoDocumentoDAO dao = new TipoDocumentoDAO();

        TipoDocumentoDTO dto = new TipoDocumentoDTO();
        dto.tipoDocumento = "TD-99";

        try {
            TipoDocumento eliminado = dao.eliminar(dto);
            assertNotNull(eliminado);
            System.out.println("✅ Eliminado correctamente: " + eliminado.getTipoDocumento());
        } catch (RuntimeException e) {
            fail("❌ Error al eliminar: " + e.getMessage());
        }
    }
}
