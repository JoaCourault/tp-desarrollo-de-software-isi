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

        // 1) Buscar huésped por DNI objetivo
        BuscarHuespedRequestDTO buscarReq = new BuscarHuespedRequestDTO();
        buscarReq.huesped = new HuespedDTO();
        buscarReq.huesped.numDoc = DNI_OBJETIVO;

        BuscarHuespedResultDTO buscarRes = controller.buscarHuesped(buscarReq);
        if (buscarRes == null || buscarRes.huespedesEncontrados == null || buscarRes.huespedesEncontrados.isEmpty()) {
            System.out.println("❌ No se encontró huésped con DNI " + DNI_OBJETIVO + ". Abortando prueba de modificación.");
            return;
        }

        HuespedDTO aModificar = buscarRes.huespedesEncontrados.get(0);
        System.out.println("➡ Encontrado: " + aModificar.nombre + " " + aModificar.apellido + " (DNI " + aModificar.numDoc + ")");

        // 2) Asegurar campos requeridos por el validador
        if (aModificar.tipoDocumento == null) aModificar.tipoDocumento = new TipoDocumentoDTO();
        aModificar.tipoDocumento.tipoDocumento = "TD-01";
        aModificar.tipoDocumento.descripcion = "DNI";

        if (aModificar.posicionIva == null || aModificar.posicionIva.isBlank()) {
            aModificar.posicionIva = "Consumidor Final";
        }

        // El validador exige CUIT con formato XX-XXXXXXXX-X
        aModificar.cuit = "20-35648972-1";

        // 3) Cargar dirección COMPLETA (usamos DI-001 existente y la completamos con valores válidos)
        DireccionDAO dirDao = new DireccionDAO();
        Direccion dirEnt = dirDao.obtener(new DireccionDTO() {{ id = "DI-001"; }});

        DireccionDTO dir = new DireccionDTO();
        dir.id           = dirEnt.getIdDireccion(); // mantenemos el id
        dir.pais         = "Argentina";
        dir.provincia    = "Buenos Aires";
        dir.localidad    = "CABA";
        dir.codigoPostal = 1405;
        dir.calle        = "Av. Siempreviva";
        dir.numero       = 742;
        dir.departamento = "A";
        dir.piso         = 1;
        aModificar.direccion = dir;

        // 4) MODIFICAR TODOS LOS CAMPOS del huésped
        aModificar.nombre         = "MARÍA ACTUALIZADA";
        aModificar.apellido       = "GÓMEZ ACTUALIZADA";
        aModificar.numDoc         = DNI_OBJETIVO;             // mantenemos DNI para identificarlo
        aModificar.fechaNacimiento = LocalDate.of(1991, 8, 20);
        aModificar.telefono       = "+54 9 11 7777-9999";
        aModificar.email          = "maria.gomez+upd@example.com";
        aModificar.ocupacion      = "Arquitecta";
        aModificar.nacionalidad   = "Argentina";

        // 5) Ejecutar modificación
        ModificarHuespedRequestDTO modReq = new ModificarHuespedRequestDTO();
        modReq.huesped = aModificar;

        try {
            ModificarHuespedResultDTO modRes = controller.modificarHuesped(modReq);
            System.out.println("Resultado modificación: " + (modRes == null ? "null" : modRes.resultado));
            if (modRes == null || modRes.resultado == null || modRes.resultado.id == null || modRes.resultado.id != 0) {
                System.out.println("❌ No se pudo modificar. Detalle: " +
                        (modRes == null || modRes.resultado == null ? "sin resultado" : modRes.resultado.mensaje));
                return;
            }
        } catch (UnsupportedOperationException e) {
            System.out.println("❌ El controller aún no implementa 'modificar'. Implementalo o usa el service.");
            return;
        } catch (Exception e) {
            System.out.println("❌ Error modificando: " + e.getMessage());
            return;
        }

        // 6) Verificar que los cambios persistieron
        BuscarHuespedResultDTO check = controller.buscarHuesped(buscarReq);
        if (check != null && check.huespedesEncontrados != null && !check.huespedesEncontrados.isEmpty()) {
            HuespedDTO hOk = check.huespedesEncontrados.get(0);
            System.out.println("✅ Post-modificación:");
            System.out.println("   Nombre: " + hOk.nombre);
            System.out.println("   Apellido: " + hOk.apellido);
            System.out.println("   CUIT: " + hOk.cuit);
            System.out.println("   Teléfono: " + hOk.telefono);
            System.out.println("   Email: " + hOk.email);
            System.out.println("   Ocupación: " + hOk.ocupacion);
            System.out.println("   Nacionalidad: " + hOk.nacionalidad);
            System.out.println("   Dirección: " + hOk.direccion.calle + " " + hOk.direccion.numero + ", " +
                    "Piso " + hOk.direccion.piso + " Depto " + hOk.direccion.departamento + ", " +
                    hOk.direccion.localidad + " (" + hOk.direccion.codigoPostal + "), " +
                    hOk.direccion.provincia + ", " + hOk.direccion.pais + " [ID=" + hOk.direccion.id + "]");
            System.out.println("🔎 Revisá la persistencia en el JSON de huéspedes.");
        } else {
            System.out.println("⚠ No pude recuperar el huésped luego de modificar para validar persistencia.");
        }
    }
}
