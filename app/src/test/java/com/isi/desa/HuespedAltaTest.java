package com.isi.desa;

import com.isi.desa.Dao.Implementations.HuespedDAO;
import com.isi.desa.Dto.Direccion.DireccionDTO;
import com.isi.desa.Dto.Huesped.AltaHuespedResultDTO;
import com.isi.desa.Dto.Huesped.AltaHuesperRequestDTO;
import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Dto.TipoDocumento.TipoDocumentoDTO;
import com.isi.desa.Service.Implementations.HuespedService;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class HuespedAltaTest {

    @Test
    void debeCrearHuespedConDireccionNueva() {
        // ====== 1) Armar DireccionDTO SIN id (nueva) ======
        DireccionDTO dir = new DireccionDTO();
        dir.id = null;                // nueva -> el DAO generará DI-XXX
        dir.calle = "Calle Falsa";
        dir.numero = Integer.valueOf(123);
        dir.departamento = "B";
        dir.piso = Integer.valueOf(2);
        dir.codigoPostal = Integer.valueOf(3000);
        dir.localidad = "Santa Fe";
        dir.provincia = "Santa Fe";
        dir.pais = "Argentina";

        // ====== 2) Armar TipoDocumentoDTO ======
        TipoDocumentoDTO td = new TipoDocumentoDTO();
        td.tipoDocumento = "TD-01";   // DNI (coincide con tu json tipoDocumento.json)
        td.descripcion = "DNI";

        // ====== 3) Armar HuespedDTO ======
        HuespedDTO h = new HuespedDTO();
        h.idHuesped = "HU-999";               // el DAO lo respeta
        h.nombre = "Pepe";
        h.apellido = "Argento";
        h.tipoDocumento = td;                 // <-- objeto, no String
        h.numDoc = "45946104";               // String (así está en tu DAO)
        h.posicionIva = "Consumidor Final";
        h.cuit = "20-50333999-7";               // String en tus JSON
        h.fechaNacimiento = LocalDate.of(1980, 5, 15);
        h.telefono = "+54 9 11 5555-1111";
        h.email = "pepe.argento@example.com";
        h.ocupacion = "Vendedor";
        h.nacionalidad = "Argentina";
        h.direccion = dir;                    // nueva dirección

        // ====== 4) Ejecutar CU-09 por el servicio ======
        HuespedService service = HuespedService.getInstance();

        // Opción A: usar la ruta "pública" que espera AltaHuesperRequestDTO
        AltaHuesperRequestDTO req = new AltaHuesperRequestDTO();
        req.huesped = h;

        // Usamos directamente service.crear(h) (tu servicio expone crear(HuespedDTO))
        // para la prueba unitaria; si después exponés controlador, podés ajustar.
        HuespedDTO creado = service.crear(h);

        // ====== 5) Asserts ======
        assertNotNull(creado, "El servicio debe devolver el HuespedDTO creado");
        assertEquals("HU-999", creado.idHuesped);
        assertNotNull(creado.direccion, "Debe tener direccion seteada");
        assertNotNull(creado.direccion.id, "La direccion debe tener un ID generado");
        assertTrue(creado.direccion.id.matches("DI-\\d{3}"), "El ID de direccion debe ser tipo DI-XXX");

        // Datos integrales
        assertEquals("Calle Falsa", creado.direccion.calle);
        assertEquals(Integer.valueOf(123), creado.direccion.numero);
        assertEquals(Integer.valueOf(3000), creado.direccion.codigoPostal);
        assertEquals("TD-01", creado.tipoDocumento.tipoDocumento);

        // Doble chequeo: el JSON de huesped se persistió
        HuespedDAO dao = new HuespedDAO();
        var persisted = dao.obtenerHuesped("503333999");
        assertNotNull(persisted, "Debe existir en el json de huespedes");
        assertEquals("HU-999", persisted.getIdHuesped());
    }
}
