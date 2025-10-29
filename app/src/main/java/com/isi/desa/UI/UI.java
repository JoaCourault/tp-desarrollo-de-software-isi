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
import com.isi.desa.Exceptions.Usuario.NotAutenticatedException;
import com.isi.desa.Service.Implementations.Logger;
import com.isi.desa.Service.Interfaces.ILogger;
import com.isi.desa.UI.Menu.Menu;
import com.isi.desa.UI.Menu.MenuItem;
import com.isi.desa.UI.Menu.MenuRunner;
import com.isi.desa.UI.Menu.MenuNavigationException;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class UI {
    public static void run() {
        ILogger logger = Logger.getInstance();
        AtomicBoolean loggedIn = new AtomicBoolean(false);

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                if (!loggedIn.get()) {
                    // --- FASE DE LOGIN ---
                    Menu loginMenu = new Menu("Bienvenido - Iniciar Sesion");
                    loginMenu.setExitOptionText("Salir"); // Personalizar texto de salida

                    // -- Item: Autenticar usuario ---
                    loginMenu.add(new MenuItem("Autenticar usuario", (Scanner sc) -> {
                        System.out.print("Nombre: ");
                        String nombre = sc.nextLine();
                        System.out.print("Apellido: ");
                        String apellido = sc.nextLine();
                        System.out.print("Password: ");
                        String password = sc.nextLine();

                        AutenticarUsuarioRequestDto req = new AutenticarUsuarioRequestDto();
                        req.nombre = nombre;
                        req.apellido = apellido;
                        req.password = password;

                        AutenticarUsuarioResponseDto res = UsuarioController.getInstance().autenticarUsuario(req);
                        if (res != null && res.usuario != null) {
                            loggedIn.set(true);
                            UsuarioDTO u = res.usuario;
                            String msg = "Usuario autenticado: " + u.nombre + " " + u.apellido + " (ID: " + u.idUsuario + ")";
                            logger.info(msg);
                            throw new MenuNavigationException(MenuNavigationException.Type.BACK_TO_ROOT);
                        } else {
                            logger.warn("Autenticacion fallida para: " + nombre + " " + apellido);
                        }
                    }));

                    MenuRunner loginRunner = new MenuRunner(loginMenu, scanner);
                    loginRunner.run();

                    // Logica de salida
                    if (!loggedIn.get()) {
                        System.out.println("Saliendo del sistema.");
                        System.exit(0);
                    }

                } else {
                    // --- FASE PRINCIPAL (YA LOGUEADO) ---
                    Menu mainMenu = new Menu("Menu Principal");
                    mainMenu.setExitOptionText("Cerrar sesion");

                    Menu huespedMenu = new Menu("Huespedes");

                    // --- "Buscar / Listar Huespedes" ---
                    huespedMenu.add(new MenuItem("Buscar / Listar Huespedes", (Scanner sc) -> {
                        if (!loggedIn.get()) { throw new NotAutenticatedException("Sesion no iniciada"); }
                        System.out.println("--- Buscar / Listar Huespedes ---");
                        System.out.println("Rellene los campos para filtrar. Deje todos vacios para listar todos los huespedes.");

                        System.out.print("Nombre: "); String nombre = sc.nextLine().trim(); if (nombre.isEmpty()) nombre = null;
                        System.out.print("Apellido: "); String apellido = sc.nextLine().trim(); if (apellido.isEmpty()) apellido = null;
                        System.out.print("Numero de documento: "); String numDoc = sc.nextLine().trim(); if (numDoc.isEmpty()) numDoc = null;
                        System.out.print("Tipo de documento (ej: DNI): "); String tipoDoc = sc.nextLine().trim(); if (tipoDoc.isEmpty()) tipoDoc = null;

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
                            // No encontrado (busqueda especifica sin resultados)
                            System.out.print("No se encontro ningun huesped con esos filtros. Desea crear uno nuevo? (s/n): ");
                            String opt = sc.nextLine().trim().toLowerCase();
                            if (opt.equals("s") || opt.equals("si")) {
                                HuespedDTO nuevo = cargarHuespedDesdeInput(sc);
                                AltaHuesperRequestDTO altaReq = new AltaHuesperRequestDTO(); altaReq.huesped = nuevo;
                                try {
                                    AltaHuespedResultDTO altaRes = HuespedController.getInstance().altaHuesped(altaReq);
                                    if (altaRes != null && altaRes.resultado != null && altaRes.resultado.id == 0) {
                                        logger.info("Huesped creado desde UI: " + nuevo.nombre + " " + nuevo.apellido);
                                    } else {
                                        logger.warn("No se pudo crear el huesped: " + (altaRes != null && altaRes.resultado!=null? altaRes.resultado.mensaje : "error desconocido"));
                                    }
                                } catch(Exception e) {
                                    logger.error("Error UI crear huesped: " + e.getMessage(), e);
                                }
                            }
                        }
                        else if (res == null || res.huespedesEncontrados == null || res.huespedesEncontrados.isEmpty()) {
                            // No se encontraron huespedes
                            if (allNull) {
                                logger.info("No hay huespedes registrados en el sistema.");
                            } else {
                                logger.info("No se encontraron huespedes con esos filtros.");
                            }
                        } else {
                            // Se encontraron huespedes (listado o busqueda)
                            if (allNull) {
                                logger.info("Listado de todos los huespedes: " + res.huespedesEncontrados.size() + " encontrados.");
                            } else {
                                logger.info("Busqueda huesped: " + res.huespedesEncontrados.size() + " encontrados.");
                            }

                            // --- BUCLE DE SELECCION (INICIO DE CAMBIOS) ---
                            while (true) {
                                System.out.println("\n--- Lista de Resultados ---");
                                // Mostrar la lista
                                for (int i = 0; i < res.huespedesEncontrados.size(); i++) {
                                    HuespedDTO h = res.huespedesEncontrados.get(i);
                                    System.out.println((i+1) + ". " + h.nombre + " " + h.apellido +
                                            " - " + h.tipoDocumento.tipoDocumento +
                                            " (" + h.numDoc + ")"
                                    );
                                }

                                int idx;
                                System.out.print("\nIngrese el numero correspondiente para seleccionar (-1 para salir): ");
                                try { idx = Integer.parseInt(sc.nextLine().trim()); } catch(Exception e) { idx = 0; }

                                if (idx == -1) {
                                    System.out.println("Saliendo de la seleccion.");
                                    return; // Sale del lambda (vuelve al menu "Huespedes")
                                }

                                if (idx < 1 || idx > res.huespedesEncontrados.size()) {
                                    System.out.println("Opcion invalida.");
                                    continue; // Vuelve al inicio del while(true) de seleccion
                                }

                                // --- HUESPED SELECCIONADO ---
                                HuespedDTO seleccionado = res.huespedesEncontrados.get(idx-1);

                                System.out.println("\n--- Huesped Seleccionado ---");
                                System.out.println(seleccionado.nombre + " " + seleccionado.apellido + " (Doc: " + seleccionado.numDoc + ")");
                                System.out.println("\n¿Que desea hacer?");
                                System.out.println("1) Modificar Huesped");
                                System.out.println("2) Eliminar Huesped");
                                System.out.println("0) Volver a la lista");
                                System.out.print("Seleccione una opcion: ");

                                String optAccion = sc.nextLine().trim();

                                if (optAccion.equals("1")) {
                                    // --- OPCION 1: MODIFICAR ---
                                    System.out.println("--- Modificar campos (deje vacio para mantener el valor actual) ---");
                                    HuespedDTO cambios = new HuespedDTO();
                                    cambios.numDoc = seleccionado.numDoc;
                                    System.out.print("Nombre [" + seleccionado.nombre + "]: "); String n = sc.nextLine().trim(); cambios.nombre = n.isEmpty()? seleccionado.nombre : n;
                                    System.out.print("Apellido [" + seleccionado.apellido + "]: "); String a = sc.nextLine().trim(); cambios.apellido = a.isEmpty()? seleccionado.apellido : a;
                                    System.out.print("Tipo de documento [" + (seleccionado.tipoDocumento!=null?seleccionado.tipoDocumento.tipoDocumento:"") + "]: "); String td = sc.nextLine().trim();
                                    if (td.isEmpty()) cambios.tipoDocumento = seleccionado.tipoDocumento; else { TipoDocumentoDTO tdd = new TipoDocumentoDTO(); tdd.tipoDocumento = td; cambios.tipoDocumento = tdd; }
                                    System.out.print("Posicion IVA [" + (seleccionado.posicionIva!=null?seleccionado.posicionIva:"") + "]: "); String iva = sc.nextLine().trim(); cambios.posicionIva = iva.isEmpty()? seleccionado.posicionIva : iva;
                                    System.out.print("CUIT [" + (seleccionado.cuit!=null?seleccionado.cuit:"") + "]: "); String cuit = sc.nextLine().trim(); cambios.cuit = cuit.isEmpty()? seleccionado.cuit : cuit;
                                    System.out.print("Fecha de nacimiento [" + (seleccionado.fechaNacimiento!=null?seleccionado.fechaNacimiento:"") + "]: "); String fn = sc.nextLine().trim();
                                    try { cambios.fechaNacimiento = fn.isEmpty()? seleccionado.fechaNacimiento : java.time.LocalDate.parse(fn); } catch(Exception ex){ cambios.fechaNacimiento = seleccionado.fechaNacimiento; }
                                    System.out.print("Telefono [" + (seleccionado.telefono!=null?seleccionado.telefono:"") + "]: "); String tel = sc.nextLine().trim(); cambios.telefono = tel.isEmpty()? seleccionado.telefono : tel;
                                    System.out.print("Email [" + (seleccionado.email!=null?seleccionado.email:"") + "]: "); String emailMod = sc.nextLine().trim(); cambios.email = emailMod.isEmpty()? seleccionado.email : emailMod;
                                    System.out.print("Ocupacion [" + (seleccionado.ocupacion!=null?seleccionado.ocupacion:"") + "]: "); String ocu = sc.nextLine().trim(); cambios.ocupacion = ocu.isEmpty()? seleccionado.ocupacion : ocu;
                                    System.out.print("Nacionalidad [" + (seleccionado.nacionalidad!=null?seleccionado.nacionalidad:"") + "]: "); String nac = sc.nextLine().trim(); cambios.nacionalidad = nac.isEmpty()? seleccionado.nacionalidad : nac;

                                    // --- Direccion (con FIX para NPE) ---
                                    DireccionDTO dir = new DireccionDTO();
                                    DireccionDTO dirOrig = seleccionado.direccion;
                                    System.out.println("--- Direccion ---");
                                    System.out.print("Pais [" + (dirOrig!=null && dirOrig.pais!=null?dirOrig.pais:"") + "]: "); String pais = sc.nextLine().trim(); dir.pais = pais.isEmpty()? (dirOrig!=null?dirOrig.pais:null) : pais;
                                    System.out.print("Provincia [" + (dirOrig!=null && dirOrig.provincia!=null?dirOrig.provincia:"") + "]: "); String prov = sc.nextLine().trim(); dir.provincia = prov.isEmpty()? (dirOrig!=null?dirOrig.provincia:null) : prov;
                                    System.out.print("Localidad [" + (dirOrig!=null && dirOrig.localidad!=null?dirOrig.localidad:"") + "]: "); String loc = sc.nextLine().trim();
                                    dir.localidad = loc.isEmpty()? (dirOrig!=null?dirOrig.localidad:null) : loc;

                                    // *** FIX NPE (mostrar) ***
                                    String cpOrigStr = (dirOrig != null && dirOrig.codigoPostal != null) ? dirOrig.codigoPostal.toString() : "";
                                    System.out.print("Codigo postal [" + cpOrigStr + "]: ");
                                    String cp = sc.nextLine().trim();
                                    // *** FIX NPE (asignar) ***
                                    try { dir.codigoPostal = cp.isEmpty()? (dirOrig!=null?dirOrig.codigoPostal:null) : Integer.parseInt(cp); }
                                    catch(Exception ex){ dir.codigoPostal = (dirOrig!=null?dirOrig.codigoPostal:null); }

                                    System.out.print("Calle [" + (dirOrig!=null && dirOrig.calle!=null?dirOrig.calle:"") + "]: "); String calle = sc.nextLine().trim(); dir.calle = calle.isEmpty()? (dirOrig!=null?dirOrig.calle:null) : calle;

                                    // *** FIX NPE (mostrar) ***
                                    String numOrigStr = (dirOrig != null && dirOrig.numero != null) ? dirOrig.numero.toString() : "";
                                    System.out.print("Numero [" + numOrigStr + "]: ");
                                    String num = sc.nextLine().trim();
                                    // *** FIX NPE (asignar) ***
                                    try { dir.numero = num.isEmpty()? (dirOrig!=null?dirOrig.numero:null) : Integer.parseInt(num); }
                                    catch(Exception ex){ dir.numero = (dirOrig!=null?dirOrig.numero:null); }

                                    dir.id = (dirOrig!=null?dirOrig.id:null);

                                    cambios.direccion = dir;
                                    cambios.idsEstadias = seleccionado.idsEstadias;
                                    cambios.idHuesped = seleccionado.idHuesped;

                                    com.isi.desa.Dto.Huesped.ModificarHuespedRequestDTO mr = new com.isi.desa.Dto.Huesped.ModificarHuespedRequestDTO();
                                    mr.huesped = cambios;
                                    try {
                                        com.isi.desa.Dto.Huesped.ModificarHuespedResultDTO r = HuespedController.getInstance().modificarHuesped(mr);
                                        System.out.println("Resultado: " + (r!=null && r.resultado!=null? r.resultado.mensaje : "sin respuesta"));
                                        // Actualizar la lista local
                                        res.huespedesEncontrados.set(idx-1, HuespedController.getInstance().buscarHuesped(req).huespedesEncontrados.get(idx-1)); // Re-buscar para frescura
                                    } catch(Exception e){ System.out.println("Error al modificar huesped: " + e.getMessage()); }

                                    System.out.println("\nModificacion guardada. Volviendo a la lista...");
                                    continue; // Vuelve al inicio del while(true) de seleccion

                                } else if (optAccion.equals("2")) {
                                    // --- OPCION 2: ELIMINAR (con confirmacion) ---
                                    System.out.println("\n--- Confirmar Eliminacion ---");
                                    System.out.println("¿Esta seguro que desea eliminar a " + seleccionado.nombre + " " + seleccionado.apellido + " (Doc: " + seleccionado.numDoc + ")?");
                                    System.out.print("Esta accion no se puede deshacer. (escriba 'si' o 's' para confirmar, o presione ENTER para cancelar): ");
                                    String confirm = sc.nextLine().trim().toLowerCase();

                                    if (confirm.equals("si") || confirm.equals("s")) {
                                        com.isi.desa.Dto.Huesped.BajaHuespedRequestDTO br = new com.isi.desa.Dto.Huesped.BajaHuespedRequestDTO();
                                        br.idHuesped = seleccionado.idHuesped;
                                        try {
                                            com.isi.desa.Dto.Huesped.BajaHuespedResultDTO r = HuespedController.getInstance().bajaHuesped(br);
                                            System.out.println("Resultado: " + (r!=null && r.resultado!=null? r.resultado.mensaje : "sin respuesta"));
                                        } catch(Exception e){ System.out.println("Error al eliminar huesped: " + e.getMessage()); }

                                        // La lista esta desactualizada, salir del lambda
                                        System.out.println("Volviendo al menu de Huespedes...");
                                        return;

                                    } else {
                                        System.out.println("Operacion cancelada. Volviendo a la lista...");
                                        continue; // Vuelve al inicio del while(true) de seleccion
                                    }

                                } else if (optAccion.equals("0")) {
                                    // --- OPCION 0: VOLVER ---
                                    System.out.println("Volviendo a la lista...");
                                    continue; // Vuelve al inicio del while(true) de seleccion

                                } else {
                                    // Opcion invalida
                                    System.out.println("Opcion invalida. Volviendo a la lista...");
                                    continue; // Vuelve al inicio del while(true) de seleccion
                                }
                            }
                            // --- FIN BUCLE DE SELECCION ---
                        }
                    }));


                    mainMenu.add(huespedMenu);

                    MenuRunner mainRunner = new MenuRunner(mainMenu, scanner);
                    mainRunner.run();

                    // Logica de cierre de sesion
                    if (loggedIn.get()) {
                        logger.info("Sesion cerrada.");
                        loggedIn.set(false);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error fatal de la UI: " + e.getMessage(), e);
        }
    }

    private static HuespedDTO cargarHuespedDesdeInput(Scanner scanner) {
        HuespedDTO nuevo = new HuespedDTO();
        System.out.println("--- Crear nuevo huesped ---");
        System.out.print("Nombre: "); nuevo.nombre = scanner.nextLine().trim();
        System.out.print("Apellido: "); nuevo.apellido = scanner.nextLine().trim();
        System.out.print("Tipo de documento (ej: DNI): "); String tdStr = scanner.nextLine().trim();
        if (!tdStr.isEmpty()) { TipoDocumentoDTO td = new TipoDocumentoDTO(); td.tipoDocumento = tdStr; nuevo.tipoDocumento = td; }
        System.out.print("Numero de documento: "); nuevo.numDoc = scanner.nextLine().trim();
        System.out.print("Posicion IVA: "); nuevo.posicionIva = scanner.nextLine().trim();
        System.out.print("CUIT (por ejemplo 20-XXXXXXXX-X): "); String cuitStr = scanner.nextLine().trim(); nuevo.cuit = cuitStr.isEmpty()? null : cuitStr;
        System.out.print("Fecha de nacimiento (YYYY-MM-DD): ");
        try { String f = scanner.nextLine().trim(); nuevo.fechaNacimiento = f.isEmpty()? null : java.time.LocalDate.parse(f); } catch(Exception ex){ nuevo.fechaNacimiento = null; }
        System.out.print("Telefono: "); nuevo.telefono = scanner.nextLine().trim();
        System.out.print("Email: "); nuevo.email = scanner.nextLine().trim();
        System.out.print("Ocupacion: "); nuevo.ocupacion = scanner.nextLine().trim();
        System.out.print("Nacionalidad: "); nuevo.nacionalidad = scanner.nextLine().trim();
        // Direccion
        DireccionDTO dir = new DireccionDTO();
        System.out.println("--- Direccion ---");
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