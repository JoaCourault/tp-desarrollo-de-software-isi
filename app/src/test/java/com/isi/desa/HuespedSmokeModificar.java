package com.isi.desa;

import com.isi.desa.Controller.HuespedController;
import com.isi.desa.Dao.Implementations.DireccionDAO;
import com.isi.desa.Dto.Direccion.DireccionDTO;
import com.isi.desa.Dto.Huesped.*;
import com.isi.desa.Dto.TipoDocumento.TipoDocumentoDTO;
import com.isi.desa.Model.Entities.Direccion.Direccion;

import java.time.LocalDate;

public class HuespedSmokeModificar {

    public static void main(String[] args) throws Exception {
        System.out.println("=== SMOKE: MODIFICAR HUÉSPED (DNI 35648972) ===");

        final String DNI_OBJETIVO = "35648972";
        HuespedController controller = new HuespedController();

        // 1) Buscar si el huésped existe
        BuscarHuespedRequestDTO buscarReq = new BuscarHuespedRequestDTO();
        buscarReq.huesped = new HuespedDTO();
        buscarReq.huesped.numDoc = DNI_OBJETIVO;

        BuscarHuespedResultDTO buscarRes = controller.buscarHuesped(buscarReq);
        if (buscarRes == null || buscarRes.huespedesEncontrados == null || buscarRes.huespedesEncontrados.isEmpty()) {
            System.out.println("❌ No existe el huésped con DNI " + DNI_OBJETIVO + ". Abortando prueba.");
            return;
        }

        HuespedDTO aModificar = buscarRes.huespedesEncontrados.get(0);
        System.out.println("✅ Encontrado: " + aModificar.nombre + " " + aModificar.apellido);

        // 2) Completo campos validados
        aModificar.tipoDocumento = new TipoDocumentoDTO();
        aModificar.tipoDocumento.tipoDocumento = "TD-01";
        aModificar.tipoDocumento.descripcion = "DNI";
        aModificar.posicionIva = "Consumidor Final";

        // CUIT opcional → OK
        aModificar.cuit = "20-35648972-1";

        // 3) Dirección válida obligatoria
        DireccionDAO dirDao = new DireccionDAO();
        Direccion existing = dirDao.obtener(new DireccionDTO() {{ id = "DI-001"; }});

        DireccionDTO dir = new DireccionDTO();
        dir.id           = existing.getIdDireccion();
        dir.pais         = "Argentina";
        dir.provincia    = "Buenos Aires";
        dir.localidad    = "CABA";
        dir.codigoPostal = 1405;
        dir.calle        = "Av. Siempreviva";
        dir.numero       = 742;
        dir.departamento = "A";
        dir.piso         = 1;
        aModificar.direccion = dir;

        // 4) Cambios reales
        aModificar.nombre = "MARÍA ACTUALIZADA";
        aModificar.apellido = "GÓMEZ ACTUALIZADA";
        aModificar.fechaNacimiento = LocalDate.of(1991, 8, 20);
        aModificar.telefono = "+54 9 11 7777-9999";
        aModificar.email = "maria.gomez+upd@example.com";
        aModificar.ocupacion = "Arquitecta";
        aModificar.nacionalidad = "Argentina";

        // 5) Enviar modificación
        ModificarHuespedRequestDTO modReq = new ModificarHuespedRequestDTO();
        modReq.huesped = aModificar;

        ModificarHuespedResultDTO modRes;
        try {
            modRes = controller.modificarHuesped(modReq);
        } catch (Exception e) {
            System.out.println("❌ Error modificando: " + e.getMessage());
            return;
        }

        if (modRes == null || modRes.resultado == null || modRes.resultado.id != 0) {
            System.out.println("❌ No se pudo modificar: "
                    + (modRes != null ? modRes.resultado.mensaje : "Sin resultado"));
            return;
        }

        System.out.println("✅ Huésped modificado correctamente.");

        // 6) Verificar persistencia
        BuscarHuespedResultDTO check = controller.buscarHuesped(buscarReq);
        if (check.huespedesEncontrados.isEmpty()) {
            System.out.println("❌ ERROR: No se recuperó tras la modificación.");
            return;
        }

        HuespedDTO ver = check.huespedesEncontrados.get(0);
        System.out.println("✅ Persistencia OK:");
        System.out.println("   Nombre: " + ver.nombre);
        System.out.println("   Apellido: " + ver.apellido);
        System.out.println("   Email: " + ver.email);
        System.out.println("   Dirección: " + ver.direccion.calle + " " + ver.direccion.numero);
    }
}
