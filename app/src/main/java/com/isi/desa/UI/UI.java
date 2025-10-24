package com.isi.desa.UI;

import com.isi.desa.Controller.HuespedController;
import com.isi.desa.Controller.UsuarioController;
import com.isi.desa.Dto.Direccion.DireccionDTO;
import com.isi.desa.Dto.TipoDocumento.TipoDocumentoDTO;
import com.isi.desa.Dto.Usuario.UsuarioDTO;
import com.isi.desa.Dto.Usuario.AutenticarUsuarioRequestDto;
import com.isi.desa.Dto.Usuario.AutenticarUsuarioResponseDto;
import com.isi.desa.Dto.Huesped.AltaHuesperRequestDTO;
import com.isi.desa.Dto.Huesped.AltaHuespedResultDTO;
import com.isi.desa.Dto.Huesped.BuscarHuespedRequestDTO;
import com.isi.desa.Dto.Huesped.BuscarHuespedResultDTO;
import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Service.Implementations.Logger;
import com.isi.desa.Service.Interfaces.ILogger;
import com.isi.desa.UI.Menu.Menu;
import com.isi.desa.UI.Menu.MenuItem;
import com.isi.desa.UI.Menu.MenuRunner;
import com.isi.desa.UI.Menu.MenuNavigationException;

import java.util.Scanner;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class UI {
    public static void run() {
        ILogger logger = Logger.getInstance();

        Menu root = new Menu("Principal");

        // Variable que indica si el usuario ya pasó el login
        AtomicBoolean loggedIn = new AtomicBoolean(false);

        Menu usuariosMenu = new Menu("Usuarios");
        usuariosMenu.add(new MenuItem("Autenticar usuario", (Scanner scanner) -> {
            System.out.print("Nombre: ");
            String nombre = scanner.nextLine();
            System.out.print("Apellido: ");
            String apellido = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();

            AutenticarUsuarioRequestDto req = new AutenticarUsuarioRequestDto();
            req.nombre = nombre;
            req.apellido = apellido;
            req.password = password;

            AutenticarUsuarioResponseDto res = UsuarioController.getInstance().autenticarUsuario(req);
            if (res != null && res.usuario != null) {
                // marcar como loggeado
                loggedIn.set(true);
                UsuarioDTO u = res.usuario;
                String msg = "Usuario autenticado: " + u.nombre + " " + u.apellido + " (ID: " + u.idUsuario + ")";
                System.out.println(msg);
                logger.info(msg);
                // Volver al menú inicial
                throw new MenuNavigationException(MenuNavigationException.Type.BACK_TO_ROOT);
            } else {
                // Si el controller retornó resultado con id == 2 (NoEncontrado), ofrecer crear usuario
                if (res != null && res.resultado != null && res.resultado.id == 2) {
                    System.out.println("Usuario no encontrado.");
                    System.out.print("¿Desea crear un nuevo usuario? (s|si: Si): ");
                    String opt = scanner.nextLine().trim().toLowerCase();
                    if (opt.equals("s") || opt.equals("si")) {
                        // Flujo de creación (reutiliza el mismo campos que el menú Crear usuario)
                        System.out.println("Crear nuevo usuario");
                        System.out.print("ID usuario: ");
                        String idUsuario = scanner.nextLine().trim();
                        System.out.print("Contraseña: ");
                        String contrasenia = scanner.nextLine().trim();
                        System.out.print("Nombre: ");
                        String nombreC = scanner.nextLine().trim();
                        System.out.print("Apellido: ");
                        String apellidoC = scanner.nextLine().trim();

                        UsuarioDTO dto = new UsuarioDTO();
                        dto.idUsuario = idUsuario;
                        dto.contrasenia = contrasenia;
                        dto.nombre = nombreC;
                        dto.apellido = apellidoC;

                        try {
                            UsuarioController.getInstance().crearUsuario(dto);
                            System.out.println("Solicitud de creación enviada.");
                        } catch (Exception e) {
                            System.out.println("Error creando usuario: " + e.getMessage());
                            logger.error("Error UI - crear usuario: " + e.getMessage(), e);
                        }
                    }
                } else {
                    String msg = "Autenticación fallida para: " + nombre + " " + apellido;
                    System.out.println("Autenticación fallida.");
                    logger.warn(msg);
                }
            }
        }));

        usuariosMenu.add(new MenuItem("Cerrar sesión", (Scanner scanner) -> {
            if (!loggedIn.get()) { System.out.println("No hay sesión iniciada."); return; }
            loggedIn.set(false);
            System.out.println("Sesión cerrada.");
            logger.info("Usuario cerró sesión.");
        }));

        Menu huespedMenu = new Menu("Huéspedes");
        // Listar
        huespedMenu.add(new MenuItem("Listar todos los huéspedes", (Scanner scanner) -> {
            if (!loggedIn.get()) { System.out.println("Debe autenticarse primero para usar esta opción."); return; }
            BuscarHuespedRequestDTO req = new BuscarHuespedRequestDTO();
            req.huesped = null; // sin filtros => listar todos
            BuscarHuespedResultDTO res = HuespedController.getInstance().buscarHuesped(req);
            if (res == null || res.huespedesEncontrados == null || res.huespedesEncontrados.isEmpty()) {
                System.out.println("No se encontraron huéspedes.");
                logger.info("Listado huéspedes: 0 encontrados");
            } else {
                List<HuespedDTO> lista = res.huespedesEncontrados;
                System.out.println("Huéspedes encontrados: " + lista.size());
                logger.info("Listado huéspedes: " + lista.size() + " encontrados");
                for (HuespedDTO h : lista) {
                    String line = "- " + h.nombre + " " + h.apellido + " (" + h.numDoc + ")";
                    System.out.println(line);
                    logger.info(line);
                }
            }
        }));

        // Buscar
        huespedMenu.add(new MenuItem("Buscar huésped por varios campos", (Scanner scanner) -> {
            if (!loggedIn.get()) { System.out.println("Debe autenticarse primero para usar esta opción."); return; }
            System.out.println("Rellene los campos para filtrar. Deje vacío para omitir un campo.");
            System.out.print("Nombre: "); String nombre = scanner.nextLine().trim(); if (nombre.isEmpty()) nombre = null;
            System.out.print("Apellido: "); String apellido = scanner.nextLine().trim(); if (apellido.isEmpty()) apellido = null;
            System.out.print("Número de documento: "); String numDoc = scanner.nextLine().trim(); if (numDoc.isEmpty()) numDoc = null;
            System.out.print("Tipo de documento (ej: DNI): "); String tipoDoc = scanner.nextLine().trim(); if (tipoDoc.isEmpty()) tipoDoc = null;

            BuscarHuespedRequestDTO req = new BuscarHuespedRequestDTO();
            HuespedDTO filtros = new HuespedDTO();
            filtros.nombre = nombre;
            filtros.apellido = apellido;
            filtros.numDoc = numDoc;

            if (tipoDoc != null) { TipoDocumentoDTO td = new TipoDocumentoDTO(); td.tipoDocumento = tipoDoc; filtros.tipoDocumento = td; }
            boolean allNull = (filtros.nombre == null && filtros.apellido == null && filtros.numDoc == null && filtros.email == null && filtros.telefono == null && filtros.nacionalidad == null && filtros.fechaNacimiento == null && filtros.tipoDocumento == null);
            req.huesped = allNull ? null : filtros;
            BuscarHuespedResultDTO res = HuespedController.getInstance().buscarHuesped(req);
            if (res!=null && res.resultado != null && res.resultado.id == 2){
                // No encontrado: preguntar si desea crear el huésped
                System.out.print("No se encontró ningún huésped. ¿Desea crear uno nuevo? (s/n): ");
                String opt = scanner.nextLine().trim().toLowerCase();
                if (opt.equals("s") || opt.equals("si")) {
                    HuespedDTO nuevo = cargarHuespedDesdeInput(scanner);
                    AltaHuesperRequestDTO altaReq = new AltaHuesperRequestDTO(); altaReq.huesped = nuevo;
                    try {
                        AltaHuespedResultDTO altaRes = HuespedController.getInstance().altaHuesped(altaReq);
                        if (altaRes != null && altaRes.resultado != null && altaRes.resultado.id == 0) {
                            System.out.println("Huésped creado correctamente.");
                            logger.info("Huésped creado desde UI: " + nuevo.nombre + " " + nuevo.apellido);
                        } else {
                            System.out.println("No se pudo crear el huésped: " + (altaRes != null && altaRes.resultado!=null? altaRes.resultado.mensaje : "error desconocido"));
                            logger.warn("Fallo creación huésped desde UI.");
                        }
                    } catch(Exception e) {
                        System.out.println("Error al crear huésped: " + e.getMessage());
                        logger.error("Error UI crear huésped: " + e.getMessage(), e);
                    }
                }
            }
            else if (res == null || res.huespedesEncontrados == null || res.huespedesEncontrados.isEmpty()) {
                logger.info("No se encontraron huéspedes con esos filtros.");
            } else {
                logger.info("Busqueda huesped: " + res.huespedesEncontrados.size() + " encontrados.");

                for (int i = 0; i < res.huespedesEncontrados.size(); i++) {
                    HuespedDTO h = res.huespedesEncontrados.get(i);
                    System.out.println((i+1) + ". " + h.nombre + " " + h.apellido + " (" + h.numDoc + ")");
                }
                int idx = 0;
                while (true) {
                    System.out.print("Ingrese el número correspondiente (-1 para salir): ");
                    try { idx = Integer.parseInt(scanner.nextLine().trim()); } catch(Exception e) { idx = 0; }
                    if (idx == -1) {
                        System.out.println("Saliendo de modificar huésped.");
                        return;
                    }
                    if (idx >= 1 && idx <= res.huespedesEncontrados.size()) break;
                }
                HuespedDTO seleccionado = res.huespedesEncontrados.get(idx-1);
                System.out.println("--- Modificar campos (deje vacío para mantener el valor actual) ---");
                HuespedDTO cambios = new HuespedDTO();
                cambios.numDoc = seleccionado.numDoc;
                System.out.print("Nombre [" + seleccionado.nombre + "]: "); String n = scanner.nextLine().trim(); cambios.nombre = n.isEmpty()? seleccionado.nombre : n;
                System.out.print("Apellido [" + seleccionado.apellido + "]: "); String a = scanner.nextLine().trim(); cambios.apellido = a.isEmpty()? seleccionado.apellido : a;
                System.out.print("Tipo de documento [" + (seleccionado.tipoDocumento!=null?seleccionado.tipoDocumento.tipoDocumento:"") + "]: "); String td = scanner.nextLine().trim();
                if (td.isEmpty()) cambios.tipoDocumento = seleccionado.tipoDocumento; else { TipoDocumentoDTO tdd = new TipoDocumentoDTO(); tdd.tipoDocumento = td; cambios.tipoDocumento = tdd; }
                System.out.print("Posición IVA [" + (seleccionado.posicionIva!=null?seleccionado.posicionIva:"") + "]: "); String iva = scanner.nextLine().trim(); cambios.posicionIva = iva.isEmpty()? seleccionado.posicionIva : iva;
                System.out.print("CUIT [" + (seleccionado.cuit!=null?seleccionado.cuit:"") + "]: "); String cuit = scanner.nextLine().trim(); cambios.cuit = cuit.isEmpty()? seleccionado.cuit : cuit;
                System.out.print("Fecha de nacimiento [" + (seleccionado.fechaNacimiento!=null?seleccionado.fechaNacimiento:"") + "]: "); String fn = scanner.nextLine().trim();
                try { cambios.fechaNacimiento = fn.isEmpty()? seleccionado.fechaNacimiento : java.time.LocalDate.parse(fn); } catch(Exception ex){ cambios.fechaNacimiento = seleccionado.fechaNacimiento; }
                System.out.print("Teléfono [" + (seleccionado.telefono!=null?seleccionado.telefono:"") + "]: "); String tel = scanner.nextLine().trim(); cambios.telefono = tel.isEmpty()? seleccionado.telefono : tel;
                System.out.print("Email [" + (seleccionado.email!=null?seleccionado.email:"") + "]: "); String emailMod = scanner.nextLine().trim(); cambios.email = emailMod.isEmpty()? seleccionado.email : emailMod;
                System.out.print("Ocupación [" + (seleccionado.ocupacion!=null?seleccionado.ocupacion:"") + "]: "); String ocu = scanner.nextLine().trim(); cambios.ocupacion = ocu.isEmpty()? seleccionado.ocupacion : ocu;
                System.out.print("Nacionalidad [" + (seleccionado.nacionalidad!=null?seleccionado.nacionalidad:"") + "]: "); String nac = scanner.nextLine().trim(); cambios.nacionalidad = nac.isEmpty()? seleccionado.nacionalidad : nac;
                // Dirección
                DireccionDTO dir = new DireccionDTO();
                DireccionDTO dirOrig = seleccionado.direccion;
                System.out.println("--- Dirección ---");
                System.out.print("Pais [" + (dirOrig!=null && dirOrig.pais!=null?dirOrig.pais:"") + "]: "); String pais = scanner.nextLine().trim(); dir.pais = pais.isEmpty()? (dirOrig!=null?dirOrig.pais:null) : pais;
                System.out.print("Provincia [" + (dirOrig!=null && dirOrig.provincia!=null?dirOrig.provincia:"") + "]: "); String prov = scanner.nextLine().trim(); dir.provincia = prov.isEmpty()? (dirOrig!=null?dirOrig.provincia:null) : prov;
                System.out.print("Localidad [" + (dirOrig!=null && dirOrig.localidad!=null?dirOrig.localidad:"") + "]: "); String loc = scanner.nextLine().trim(); dir.localidad = loc.isEmpty()? (dirOrig!=null?dirOrig.localidad:null) : loc;
                System.out.print("Codigo postal [" + (dirOrig!=null && dirOrig.codigoPostal!=null?dirOrig.codigoPostal:"") + "]: "); String cp = scanner.nextLine().trim();
                try { dir.codigoPostal = cp.isEmpty()? (dirOrig!=null?dirOrig.codigoPostal:null) : Integer.parseInt(cp); } catch(Exception ex){ dir.codigoPostal = dirOrig!=null?dirOrig.codigoPostal:null; }
                System.out.print("Calle [" + (dirOrig!=null && dirOrig.calle!=null?dirOrig.calle:"") + "]: "); String calle = scanner.nextLine().trim(); dir.calle = calle.isEmpty()? (dirOrig!=null?dirOrig.calle:null) : calle;
                System.out.print("Numero [" + (dirOrig!=null && dirOrig.numero!=null?dirOrig.numero:"") + "]: "); String num = scanner.nextLine().trim();
                try { dir.numero = num.isEmpty()? (dirOrig!=null?dirOrig.numero:null) : Integer.parseInt(num); } catch(Exception ex){ dir.numero = dirOrig!=null?dirOrig.numero:null; }
                cambios.direccion = dir;
                com.isi.desa.Dto.Huesped.ModificarHuespedRequestDTO mr = new com.isi.desa.Dto.Huesped.ModificarHuespedRequestDTO();
                mr.huesped = cambios;
                try {
                    com.isi.desa.Dto.Huesped.ModificarHuespedResultDTO r = HuespedController.getInstance().modificarHuesped(mr);
                    System.out.println("Resultado: " + (r!=null && r.resultado!=null? r.resultado.mensaje : "sin respuesta"));
                } catch(Exception e){ System.out.println("Error al modificar huésped: " + e.getMessage()); }
            }
        }));

        // Eliminar huesped
        huespedMenu.add(new MenuItem("Eliminar huésped (simple)", (Scanner scanner) -> {
            if (!loggedIn.get()) { System.out.println("Debe autenticarse primero para usar esta opción."); return; }
            System.out.print("Ingrese número de documento del huésped a eliminar: ");
            String numDoc = scanner.nextLine().trim();
            com.isi.desa.Dto.Huesped.BajaHuespedRequestDTO br = new com.isi.desa.Dto.Huesped.BajaHuespedRequestDTO();
            com.isi.desa.Dto.Huesped.HuespedDTO h = new com.isi.desa.Dto.Huesped.HuespedDTO(); h.numDoc = numDoc; br.idHuesped = h.idHuesped;
            try {
                com.isi.desa.Dto.Huesped.BajaHuespedResultDTO r = HuespedController.getInstance().bajaHuesped(br);
                System.out.println("Resultado: " + (r!=null && r.resultado!=null? r.resultado.mensaje : "sin respuesta"));
            } catch(Exception e){ System.out.println("Error al eliminar huésped: " + e.getMessage()); }
        }));

        root.add(usuariosMenu);
        root.add(huespedMenu);

        MenuRunner runner = new MenuRunner(root);
        runner.run();

        System.out.println("Proceso finalizado");
    }

    private static HuespedDTO cargarHuespedDesdeInput(Scanner scanner) {
        HuespedDTO nuevo = new HuespedDTO();
        System.out.println("--- Crear nuevo huésped ---");
        System.out.print("Nombre: "); nuevo.nombre = scanner.nextLine().trim();
        System.out.print("Apellido: "); nuevo.apellido = scanner.nextLine().trim();
        System.out.print("Tipo de documento (ej: DNI): "); String tdStr = scanner.nextLine().trim();
        if (!tdStr.isEmpty()) { TipoDocumentoDTO td = new TipoDocumentoDTO(); td.tipoDocumento = tdStr; nuevo.tipoDocumento = td; }
        System.out.print("Número de documento: "); nuevo.numDoc = scanner.nextLine().trim();
        System.out.print("Posición IVA: "); nuevo.posicionIva = scanner.nextLine().trim();
        System.out.print("CUIT (por ejemplo 20-XXXXXXXX-X): "); String cuitStr = scanner.nextLine().trim(); nuevo.cuit = cuitStr.isEmpty()? null : cuitStr;
        System.out.print("Fecha de nacimiento (YYYY-MM-DD): ");
        try { String f = scanner.nextLine().trim(); nuevo.fechaNacimiento = f.isEmpty()? null : java.time.LocalDate.parse(f); } catch(Exception ex){ nuevo.fechaNacimiento = null; }
        System.out.print("Teléfono: "); nuevo.telefono = scanner.nextLine().trim();
        System.out.print("Email: "); nuevo.email = scanner.nextLine().trim();
        System.out.print("Ocupación: "); nuevo.ocupacion = scanner.nextLine().trim();
        System.out.print("Nacionalidad: "); nuevo.nacionalidad = scanner.nextLine().trim();
        // Dirección
        DireccionDTO dir = new DireccionDTO();
        System.out.println("--- Dirección ---");
        System.out.print("Pais: "); dir.pais = scanner.nextLine().trim();
        System.out.print("Provincia: "); dir.provincia = scanner.nextLine().trim();
        System.out.print("Localidad: "); dir.localidad = scanner.nextLine().trim();
        System.out.print("Codigo postal: ");
        try { String cp = scanner.nextLine().trim(); dir.codigoPostal = cp.isEmpty()? null : Integer.parseInt(cp); } catch(Exception ex){ dir.codigoPostal = null; }
        System.out.print("Calle: "); dir.calle = scanner.nextLine().trim();
        System.out.print("Numero: ");
        try { String num = scanner.nextLine().trim(); dir.numero = num.isEmpty()? null : Integer.parseInt(num); } catch(Exception ex){ dir.numero = null; }
        nuevo.direccion = dir;
        return nuevo;
    }
}
