package com.isi.desa;

import com.isi.desa.Dao.Implementations.DireccionDAO;
import com.isi.desa.Dto.Direccion.DireccionDTO;
import com.isi.desa.Model.Entities.Direccion.Direccion;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DireccionDAOTest {

    @Test
    void testObtenerDireccion() {
        // ✅ Instancia del DAO
        DireccionDAO direccionDAO = new DireccionDAO();

        // ✅ Creamos un DTO con el ID que queremos buscar (debe existir en el JSON)
        DireccionDTO dtoConsulta = new DireccionDTO();
        dtoConsulta.id = "DI-003"; // 👈 ejemplo: una direccion existente en tu JSON

        try {
            // ✅ Obtenemos la entidad completa desde el JSON
            Direccion direccion = direccionDAO.obtener(dtoConsulta);

            // ✅ Verificamos que se haya obtenido correctamente
            assertNotNull(direccion, "La direccion no debe ser nula");
            assertEquals("Madrid", direccion.getLocalidad(), "La localidad deberia coincidir");
            assertEquals("Espania", direccion.getPais(), "El pais deberia coincidir");

            System.out.println("✅ Direccion obtenida correctamente:");
            System.out.println("  ID: " + direccion.getIdDireccion());
            System.out.println("  Calle: " + direccion.getCalle());
            System.out.println("  Localidad: " + direccion.getLocalidad());
            System.out.println("  Provincia: " + direccion.getProvincia());
            System.out.println("  Pais: " + direccion.getPais());

        } catch (RuntimeException e) {
            fail("❌ Error al obtener direccion: " + e.getMessage());
        }
    }
    @Test
    void testCrearDireccion() {
        DireccionDAO direccionDAO = new DireccionDAO();

        // ✅ Creamos una nueva direccion DTO
        DireccionDTO nueva = new DireccionDTO();
        nueva.id = "DI-999";
        nueva.calle = "Av. Test Unitario";
        nueva.numero = 123;
        nueva.departamento = "B";
        nueva.piso = 2;
        nueva.codigoPostal = 9999;
        nueva.localidad = "Santa Fe";
        nueva.provincia = "Santa Fe";
        nueva.pais = "Argentina";

        try {
            Direccion creada = direccionDAO.crear(nueva);
            assertNotNull(creada, "La direccion creada no debe ser nula");
            assertEquals("DI-999", creada.getIdDireccion(), "El ID deberia coincidir");
            System.out.println("✅ Direccion creada correctamente: " + creada.getCalle() + " (" + creada.getIdDireccion() + ")");
        } catch (RuntimeException e) {
            fail("❌ Error al crear direccion: " + e.getMessage());
        }
    }

    @Test
    void testEliminarDireccion() {
        DireccionDAO direccionDAO = new DireccionDAO();

        // ✅ Creamos el DTO con el ID a eliminar (usa el mismo ID de arriba si queres probar ambos juntos)
        DireccionDTO eliminar = new DireccionDTO();
        eliminar.id = "DI-999";

        try {
            Direccion eliminada = direccionDAO.eliminar(eliminar);
            assertNotNull(eliminada, "La direccion eliminada no debe ser nula");
            assertEquals("DI-999", eliminada.getIdDireccion(), "El ID eliminado deberia coincidir");
            System.out.println("✅ Direccion eliminada correctamente: " + eliminada.getIdDireccion());
        } catch (RuntimeException e) {
            fail("❌ Error al eliminar direccion: " + e.getMessage());
        }
    }
}
