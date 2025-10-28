package com.isi.desa;

import com.isi.desa.Dao.Implementations.HuespedDAO;
import com.isi.desa.Dto.Direccion.DireccionDTO;
import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Dto.TipoDocumento.TipoDocumentoDTO;
import com.isi.desa.Exceptions.Huesped.HuespedDuplicadoException;
import com.isi.desa.Service.Implementations.HuespedService;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class HuespedAltaTest {

    @Test
    void debeCrearHuespedConDireccionNueva() {
        // 1) Direccion nueva (sin id)
        DireccionDTO dir = new DireccionDTO();
        dir.id = null;
        dir.calle = "Calle Falsa";
        dir.numero = 123;
        dir.departamento = "B";
        dir.piso = 2;
        dir.codigoPostal = 3000;
        dir.localidad = "Santa Fe";
        dir.provincia = "Santa Fe";
        dir.pais = "Argentina";

        // 2) Tipo Doc
        TipoDocumentoDTO td = new TipoDocumentoDTO();
        td.tipoDocumento = "TD-01"; // DNI (segun tu json)
        td.descripcion = "DNI";

        // 3) Huesped
        HuespedDTO h = new HuespedDTO();
        h.idHuesped = "HU-999";
        h.nombre = "Pepe";
        h.apellido = "Argento";
        h.tipoDocumento = td;
        h.numDoc = "45946104";                  // usar este luego para verificar
        h.posicionIva = "Consumidor Final";
        h.cuit = "20-50333999-7";               // formato XX-XXXXXXXX-X ok
        h.fechaNacimiento = LocalDate.of(1980, 5, 15);
        h.telefono = "+54 9 11 5555-1111";
        h.email = "pepe.argento@example.com";
        h.ocupacion = "Vendedor";
        h.nacionalidad = "Argentina";
        h.direccion = dir;

        HuespedService service = HuespedService.getInstance();

        try {
            // 4) Alta por servicio
            HuespedDTO creado = service.crear(h);

            // 5) Asserts del retorno
            assertNotNull(creado, "El servicio debe devolver el HuespedDTO creado");
            assertEquals("HU-999", creado.idHuesped);
            assertNotNull(creado.direccion, "Debe tener direccion seteada");
            assertNotNull(creado.direccion.id, "La direccion debe tener un ID (DI-xxx)");
            assertTrue(creado.direccion.id.startsWith("DI-"));

            assertEquals("Calle Falsa", creado.direccion.calle);
            assertEquals(Integer.valueOf(123), creado.direccion.numero);
            assertEquals(Integer.valueOf(3000), creado.direccion.codigoPostal);
            assertEquals("TD-01", creado.tipoDocumento.tipoDocumento);

            // 6) Verificar en persistencia por DNI correcto
            HuespedDAO dao = new HuespedDAO();
            var persisted = dao.obtenerHuesped("45946104");
            assertNotNull(persisted, "Debe existir en el json de huespedes");
            assertEquals("HU-999", persisted.getIdHuesped());

        } catch (HuespedDuplicadoException e) {
            fail("No deberia lanzarse HuespedDuplicadoException en alta valida: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado en alta de huesped: " + e.getMessage());
        }
    }
}
