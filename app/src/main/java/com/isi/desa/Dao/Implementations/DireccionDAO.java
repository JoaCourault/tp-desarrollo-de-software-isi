package com.isi.desa.Dao.Implementations;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isi.desa.Dao.Interfaces.IDireccionDAO;
import com.isi.desa.Dto.Direccion.DireccionDTO;
import com.isi.desa.Model.Entities.Direccion.Direccion;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.isi.desa.Utils.Mappers.DireccionMapper.dtoToEntity;

public class DireccionDAO implements IDireccionDAO {

    private static final String RES_DIR   = "jsonDataBase";
    private static final String JSON_FILE = "direccion.json";

    private final ObjectMapper mapper = new ObjectMapper();

    // ===== Helpers de ruta (solo build) =====
    private File getJsonFileForRead() {
        try {
            File f1 = Paths.get("app","build","resources","main",RES_DIR,JSON_FILE).toFile();
            if (f1.exists()) return f1;

            File f2 = Paths.get("build","resources","main",RES_DIR,JSON_FILE).toFile();
            if (f2.exists()) return f2;

            // No se crean archivos ni carpetas: informar al usuario
            throw new RuntimeException("No se encontro el archivo de datos '" + RES_DIR + "/" + JSON_FILE + "' en build. Ejecute el build y reintente.");
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException("Error al localizar el archivo JSON en build: " + e.getMessage(), e);
        }
    }

    private File getJsonFileForWrite() {
        File f1 = Paths.get("app","build","resources","main",RES_DIR,JSON_FILE).toFile();
        if (f1.exists() && f1.isFile()) return f1;

        File f2 = Paths.get("build","resources","main",RES_DIR,JSON_FILE).toFile();
        if (f2.exists() && f2.isFile()) return f2;

        throw new RuntimeException("No se encontro archivo de salida en build para escribir '" + RES_DIR + "/" + JSON_FILE + "'. No se crean carpetas nuevas. Ejecute el build primero.");
    }

    // ===== IO =====
    private List<Direccion> leerDirecciones() {
        File file = getJsonFileForRead();
        try {
            if (file.length() == 0) return new ArrayList<>();
            return mapper.readValue(file, new TypeReference<List<Direccion>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Error al leer " + JSON_FILE, e);
        }
    }

    private void guardarDirecciones(List<Direccion> direcciones) {
        try {
            File file = getJsonFileForWrite();
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, direcciones);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar " + JSON_FILE, e);
        }
    }

    // ===== Implementacion IDireccionDAO =====
    @Override
    public Direccion crear(DireccionDTO direccion) {
        List<Direccion> direcciones = leerDirecciones();

        // Generar ID incremental si no viene (DI-###)
        if (direccion.id == null || direccion.id.isBlank()) {
            int max = 0;
            for (Direccion d : direcciones) {
                String id = d.getIdDireccion(); // p.ej.: "DI-015"
                if (id != null && id.startsWith("DI-")) {
                    try {
                        int n = Integer.parseInt(id.substring(3));
                        if (n > max) max = n;
                    } catch (NumberFormatException ignored) {}
                }
            }
            direccion.id = String.format("DI-%03d", max + 1);
        }

        boolean existe = direcciones.stream()
                .anyMatch(d -> d.getIdDireccion().equalsIgnoreCase(direccion.id));
        if (existe) {
            throw new RuntimeException("Ya existe una direccion con el ID: " + direccion.id);
        }

        Direccion nueva = dtoToEntity(direccion);
        direcciones.add(nueva);
        guardarDirecciones(direcciones);
        return nueva;
    }

    @Override
    public Direccion modificar(DireccionDTO direccion) {
        List<Direccion> direcciones = leerDirecciones();

        Optional<Direccion> existente = direcciones.stream()
                .filter(d -> d.getIdDireccion().equalsIgnoreCase(direccion.id))
                .findFirst();

        if (existente.isEmpty()) {
            throw new RuntimeException("No se encontro la direccion con ID: " + direccion.id);
        }

        Direccion actualizada = dtoToEntity(direccion);
        direcciones.set(direcciones.indexOf(existente.get()), actualizada);
        guardarDirecciones(direcciones);
        return actualizada;
    }

    @Override
    public Direccion eliminar(DireccionDTO direccion) {
        List<Direccion> direcciones = leerDirecciones();

        boolean eliminado = direcciones.removeIf(d -> d.getIdDireccion().equalsIgnoreCase(direccion.id));
        if (!eliminado) {
            throw new RuntimeException("No se encontro la direccion a eliminar: " + direccion.id);
        }

        guardarDirecciones(direcciones);
        return dtoToEntity(direccion);
    }

    @Override
    public Direccion obtener(DireccionDTO direccion) {
        List<Direccion> direcciones = leerDirecciones();
        return direcciones.stream()
                .filter(d -> d.getIdDireccion().equalsIgnoreCase(direccion.id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No se encontro direccion con ID: " + direccion.id));
    }
}
