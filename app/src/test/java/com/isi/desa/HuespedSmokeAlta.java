package com.isi.desa;

import com.isi.desa.Controller.HuespedController;
import com.isi.desa.Dao.Implementations.DireccionDAO;
import com.isi.desa.Dto.Direccion.DireccionDTO;
import com.isi.desa.Dto.Huesped.AltaHuespedRequestDTO;
import com.isi.desa.Dto.Huesped.AltaHuespedResultDTO;
import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Dto.TipoDocumento.TipoDocumentoDTO;

import java.time.LocalDate;
import java.util.UUID;

public class HuespedSmokeAlta {

    public static void main(String[] args) throws Exception {

        System.out.println("=== SMOKE: ALTA DE HUÉSPED CON CREACIÓN DE DIRECCIÓN ===");

        HuespedController controller = new HuespedController();
        DireccionDAO dirDao = new DireccionDAO();

        // 1️⃣ Primero generamos una nueva dirección y la guardamos
        DireccionDTO dir = new DireccionDTO();
        dir.id = "DI-" + UUID.randomUUID();
        dir.pais = "Argentina";
        dir.provincia = "Buenos Aires";
        dir.localidad = "Avellaneda";
        dir.codigoPostal = 1870;
        dir.calle = "Mitre";
        dir.numero = 1234;
        dir.departamento = "B";
        dir.piso = 2;

        dirDao.crear(dir); // ✅ PERSISTE en direccion.json

        // 2️⃣ Creamos el huésped DTO usando esa dirección
        String dni = "DNI-" + UUID.randomUUID();

        HuespedDTO h = new HuespedDTO();
        h.idHuesped = "H-" + UUID.randomUUID();
        h.nombre = "ANA";
        h.apellido = "PRUEBA-SMOKE";
        h.tipoDocumento = new TipoDocumentoDTO();
        h.tipoDocumento.tipoDocumento = "TD-01";
        h.tipoDocumento.descripcion = "DNI";
        h.numDoc = dni;
        h.posicionIva = "Consumidor Final";
        h.cuit = "20-12345678-9";
        h.fechaNacimiento = LocalDate.of(1995, 1, 1);
        h.telefono = "+54 9 11 5555-5555";
        h.email = "ana.smoke@example.com";
        h.ocupacion = "Tester";
        h.nacionalidad = "Argentina";
        h.direccion = dir;  // ✅ Usa la dirección recién creada

        AltaHuespedRequestDTO req = new AltaHuespedRequestDTO();
        req.huesped = h;

        // 3️⃣ Ejecutamos el alta
        AltaHuespedResultDTO res = controller.altaHuesped(req);

        System.out.println("Resultado alta: " + res.resultado);
        System.out.println("Se creó huésped con DNI = " + dni);
        System.out.println("✅ Revisá:");
        System.out.println("   - app/data/jsonDataBase/huesped.json");
        System.out.println("   - app/data/jsonDataBase/direccion.json");
        System.out.println("   para validar persistencia correcta de ambos.");
    }
}
