package com.isi.desa;

import com.isi.desa.Dao.Implementations.HuespedDAO;
import com.isi.desa.Dao.Interfaces.IHuespedDAO;
import com.isi.desa.Dto.Direccion.DireccionDTO;
import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Dto.TipoDocumento.TipoDocumentoDTO;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class HuespedDAOTest {

    private final IHuespedDAO dao = new HuespedDAO();

    private HuespedDTO buildGuest(String id, String dni) {

        HuespedDTO h = new HuespedDTO();
        h.idHuesped = id;
        h.nombre = "Test";
        h.apellido = "JUnit";
        h.numDoc = dni;
        h.posicionIva = "Consumidor Final";
        h.cuit = "20000000001";
        h.fechaNacimiento = LocalDate.of(1990, 1, 1);
        h.telefono = "+54 9 11 0000-0000";
        h.email = "test@example.com";
        h.ocupacion = "Tester";
        h.nacionalidad = "Argentina";

        // *** IMPORTANTE: instanciar TipoDocumentoDTO para evitar NPE ***
        h.tipoDocumento = new TipoDocumentoDTO();
        h.tipoDocumento.tipoDocumento = "TD-01"; // existe en tipoDocumento.json
        h.tipoDocumento.descripcion = "DNI";

        // Dirección opcional: si resolvés por ID, usá uno existente
        h.direccion = new DireccionDTO();
        h.direccion.id = "DI-001"; // existe en direccion.json

        return h;
    }

    @Test
    void testCrearHuesped() {
        String id = "HU-" + UUID.randomUUID();
        String dni = "DNI-" + UUID.randomUUID();

        HuespedDTO nuevo = buildGuest(id, dni);
        Huesped creado = dao.crear(nuevo);

        assertNotNull(creado);
        assertEquals(dni, creado.getNumDoc());
        System.out.println("✅ Huésped creado: " + creado.getIdHuesped() + " / " + creado.getNumDoc());

        // Cleanup: borrar lo creado para no dejar basura
        HuespedDTO borrar = new HuespedDTO();
        borrar.numDoc = dni;
        dao.eliminar(borrar);
    }

    @Test
    void testObtenerHuesped() {
        String dni = "DNI-" + UUID.randomUUID();
        String id  = "HU-" + UUID.randomUUID();

        // arrange
        dao.crear(buildGuest(id, dni));

        // act
        Huesped h = dao.obtenerHuesped(dni);

        // assert
        assertNotNull(h);
        assertEquals(dni, h.getNumDoc());

        // cleanup
        HuespedDTO del = new HuespedDTO(); del.numDoc = dni;
        dao.eliminar(del);
    }

    @Test
    void testEliminarHuesped() {
        // Arrange: crear uno temporal
        String id = "HU-DEL-" + UUID.randomUUID();
        String dni = "DNI-" + "UUID.randomUUID()";
        dao.crear(buildGuest(id, dni));

        // Act: borrar
        HuespedDTO eliminar = new HuespedDTO();
        eliminar.numDoc = dni;
        Huesped eliminado = dao.eliminar(eliminar);

        // Assert
        assertNotNull(eliminado);
        assertEquals(dni, eliminado.getNumDoc());
        System.out.println("✅ Huésped eliminado: " + eliminado.getNumDoc());
    }

}
