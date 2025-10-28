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

    // ===== Helpers de ruta (sin clase extra) =====
    private File getJsonFileForRead() {
        try {
            String resourcePath = RES_DIR + "/" + JSON_FILE;
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            URL url = cl.getResource(resourcePath);
            if (url != null && !"jar".equalsIgnoreCase(url.getProtocol())) {
                File f = Paths.get(url.toURI()).toFile();
                if (f.exists()) return f;
            }
            // fallback si no existe o esta en JAR
            return getJsonFileForWrite();
        } catch (Exception e) {
            return getJsonFileForWrite();
        }
    }

    private File getJsonFileForWrite() {
        // 1) ruta dev (IDE)
        File dev = Paths.get("src","main","resources",RES_DIR,JSON_FILE).toFile();
        try {
            ensureFile(dev);
            return dev;
        } catch (Exception ignore) {
            // 2) fallback para JAR/produccion local
            File external = Paths.get("data",RES_DIR,JSON_FILE).toFile();
            try {
                ensureFile(external);
                return external;
            } catch (Exception ex) {
                throw new RuntimeException("No se pudo crear archivo JSON: " + external.getAbsolutePath(), ex);
            }
        }
    }

    private void ensureFile(File f) throws Exception {
        File p = f.getParentFile();
        if (p != null && !p.exists()) p.mkdirs();
        if (!f.exists()) f.createNewFile();
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
