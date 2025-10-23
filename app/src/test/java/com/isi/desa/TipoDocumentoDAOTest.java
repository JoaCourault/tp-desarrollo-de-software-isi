package com.isi.desa;

import com.isi.desa.Dao.Implementations.TipoDocumentoDAO;
import com.isi.desa.Dao.Interfaces.ITipoDocumentoDAO;
import com.isi.desa.Dto.TipoDocumento.TipoDocumentoDTO;
import com.isi.desa.Model.Entities.Tipodocumento.TipoDocumento;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TipoDocumentoDAOTest {

    private final ITipoDocumentoDAO dao = new TipoDocumentoDAO();

    private void ensureExistsTD99() {
        try {
            dao.obtener("TD-99");
        } catch (Exception e) {
            TipoDocumentoDTO dto = new TipoDocumentoDTO();
            dto.tipoDocumento = "TD-99";
            dto.descripcion = "Prueba Temporal";
            dao.crear(dto);
        }
    }

    private void cleanupTD99() {
        try {
            TipoDocumentoDTO dto = new TipoDocumentoDTO();
            dto.tipoDocumento = "TD-99";
            dao.eliminar(dto);
        } catch (Exception ignored) { }
    }

    @Test
    void testCrearTipoDocumento() {
        cleanupTD99(); // arranco limpio
        TipoDocumentoDTO dto = new TipoDocumentoDTO();
        dto.tipoDocumento = "TD-99";
        dto.descripcion = "Prueba Temporal";
        TipoDocumento creado = dao.crear(dto);
        Assertions.assertEquals("TD-99", creado.getTipoDocumento());
        System.out.println("✅ Creado correctamente: " + creado.getTipoDocumento() + " - " + creado.getDescripcion());
        cleanupTD99();
    }

    @Test
    void testModificarTipoDocumento() {
        ensureExistsTD99(); // asegurar existencia
        TipoDocumentoDTO dto = new TipoDocumentoDTO();
        dto.tipoDocumento = "TD-99";
        dto.descripcion = "Descripción actualizada";
        TipoDocumento mod = dao.modificar(dto);
        Assertions.assertEquals("Descripción actualizada", mod.getDescripcion());
        System.out.println("✅ Modificado correctamente: " + mod.getTipoDocumento() + " - " + mod.getDescripcion());
        cleanupTD99();
    }

    @Test
    void testObtenerTipoDocumento() {
        TipoDocumento tipo = dao.obtener("TD-01");
        Assertions.assertEquals("TD-01", tipo.getTipoDocumento());
        System.out.println("✅ Obtenido correctamente: " + tipo.getTipoDocumento() + " - " + tipo.getDescripcion());
    }

    @Test
    void testEliminarTipoDocumento() {
        ensureExistsTD99(); // asegurar existencia
        TipoDocumentoDTO dto = new TipoDocumentoDTO();
        dto.tipoDocumento = "TD-99";
        TipoDocumento eliminado = dao.eliminar(dto);
        Assertions.assertEquals("TD-99", eliminado.getTipoDocumento());
        System.out.println("✅ Eliminado correctamente: " + eliminado.getTipoDocumento());
    }
}
