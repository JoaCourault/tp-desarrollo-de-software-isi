package com.isi.desa;

import com.isi.desa.Dao.Implementations.HuespedDAO;
import com.isi.desa.Dto.Direccion.DireccionDTO;
import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Dto.Huesped.ModificarHuespedRequestDTO;
import com.isi.desa.Dto.Huesped.ModificarHuespedResultDTO;
import com.isi.desa.Dto.TipoDocumento.TipoDocumentoDTO;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Service.Implementations.HuespedService;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ModificarHuespedTest {

    private final HuespedService service = HuespedService.getInstance();
    private final HuespedDAO dao = new HuespedDAO();

    /** Caso feliz: modifico telefono, email y direccion de HU-001 */
    @Test
    void modificar_ok() {
        // Pre: HU-001 existe en tu JSON de ejemplo
        HuespedDTO dto = new HuespedDTO();
        dto.idHuesped   = "HU-001";
        dto.nombre      = "MARIA";
        dto.apellido    = "GOMEZ";
        dto.tipoDocumento = new TipoDocumentoDTO();
        dto.tipoDocumento.tipoDocumento = "TD-01"; // DNI
        dto.tipoDocumento.descripcion   = "DNI";
        dto.numDoc      = "35648972";
        dto.posicionIva = "Consumidor Final";
        dto.cuit        = "20-35648972-1"; // opcional, pero viene cargado en tu JSON
        dto.fechaNacimiento = LocalDate.of(1990, 7, 15);
        dto.telefono    = "+54 9 11 7777-7777";     // cambio
        dto.email       = "maria.actualizado@example.com"; // cambio
        dto.ocupacion   = "Ingeniera Civil";
        dto.nacionalidad= "ARGENTINA";

        // Direccion: referencio una que exista (DI-005 por ejemplo)
        DireccionDTO dir = new DireccionDTO();
        dir.id        = "DI-005";
        dir.pais      = "Argentina";
        dir.provincia = "Santa Fe";
        dir.localidad = "Santa Fe de la Vera Cruz";
        dir.codigoPostal = 3000;
        dir.calle     = "Rivadavia";
        dir.numero    = 2250;
        dir.departamento = "C";
        dir.piso      = 7;
        dto.direccion = dir;

        ModificarHuespedRequestDTO req = new ModificarHuespedRequestDTO();
        req.huesped = dto;
        req.aceptarIgualmente = false;

        ModificarHuespedResultDTO res = service.modificar(req);
        assertEquals(0, res.resultado.id, "Debe modificar con exito");

        // Verifico que persistio
        Huesped h = dao.getById("HU-001");
        assertEquals("+54 9 11 7777-7777", h.getTelefono());
        assertEquals("maria.actualizado@example.com", h.getEmail());
        assertEquals("DI-005", h.getDireccion().getIdDireccion());
    }

    /** Omisiones: nombre vacio y sin direccion → id=2 con lista de errores */
    @Test
    void modificar_omisiones() {
        HuespedDTO dto = new HuespedDTO();
        dto.idHuesped   = "HU-001";
        dto.nombre      = "";         // omision
        dto.apellido    = "GOMEZ";
        dto.tipoDocumento = new TipoDocumentoDTO();
        dto.tipoDocumento.tipoDocumento = "TD-01";
        dto.tipoDocumento.descripcion   = "DNI";
        dto.numDoc      = "35648972";
        dto.posicionIva = "Consumidor Final";
        dto.fechaNacimiento = LocalDate.of(1990, 7, 15);
        dto.telefono    = "+54 9 11 4567-8910";
        dto.email       = null;
        dto.ocupacion   = "Ingeniera Civil";
        dto.nacionalidad= "ARGENTINA";
        dto.direccion   = null;       // omision

        ModificarHuespedRequestDTO req = new ModificarHuespedRequestDTO();
        req.huesped = dto;

        ModificarHuespedResultDTO res = service.modificar(req);
        assertEquals(2, res.resultado.id, "Debe fallar por omisiones");
        assertTrue(res.resultado.mensaje.contains("El nombre es obligatorio"));
        assertTrue(res.resultado.mensaje.contains("La direccion es obligatoria"));
    }

    /** Duplicado: HU-003 pasa a tener (TD-01, 29456832) que ya pertenece a HU-002.
     * Primero avisa (id=3), luego “aceptar igualmente” (id=0). */
    @Test
    void modificar_duplicado_con_aceptar_igualmente() {
        HuespedDTO dto = new HuespedDTO();
        dto.idHuesped   = "HU-003";             // existe
        dto.nombre      = "LUCIA";
        dto.apellido    = "MARTINEZ";
        dto.tipoDocumento = new TipoDocumentoDTO();
        dto.tipoDocumento.tipoDocumento = "TD-01";
        dto.tipoDocumento.descripcion   = "DNI";
        dto.numDoc      = "29456832";           // de HU-002 → genera duplicado
        dto.posicionIva = "Exento";
        dto.cuit        = "0";                  // opcional
        dto.fechaNacimiento = LocalDate.of(1992, 10, 3);
        dto.telefono    = "+34 600 123 456";
        dto.email       = "lucia.martinez@example.es";
        dto.ocupacion   = "Estudiante";
        dto.nacionalidad= "ESPANIOLA";

        // direccion existente DI-003
        DireccionDTO dir = new DireccionDTO();
        dir.id = "DI-003";
        dir.pais = "Espania";
        dir.provincia = "Madrid";
        dir.localidad = "Madrid";
        dir.codigoPostal = 28013;
        dir.calle = "Gran Via";
        dir.numero = 56;
        dir.departamento = null;
        dir.piso = null;
        dto.direccion = dir;

        // 1) intento sin aceptar igualmente → advertencia
        ModificarHuespedRequestDTO req = new ModificarHuespedRequestDTO();
        req.huesped = dto;
        req.aceptarIgualmente = false;

        ModificarHuespedResultDTO r1 = service.modificar(req);
        assertEquals(3, r1.resultado.id, "Debe avisar duplicado");
        assertTrue(r1.resultado.mensaje.contains("¡CUIDADO!"));

        // 2) aceptar igualmente
        req.aceptarIgualmente = true;
        ModificarHuespedResultDTO r2 = service.modificar(req);
        assertEquals(0, r2.resultado.id, "Debe permitir modificar aceptando igualmente");

        // Verifico que se guardo ese doc en HU-003
        Huesped h3 = dao.getById("HU-003");
        assertEquals("29456832", h3.getNumDoc());
        assertEquals("TD-01", h3.getTipoDocumento().getTipoDocumento());
    }
}
