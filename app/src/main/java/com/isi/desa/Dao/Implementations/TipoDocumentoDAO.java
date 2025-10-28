package com.isi.desa.Dao.Implementations;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isi.desa.Dao.Interfaces.ITipoDocumentoDAO;
import com.isi.desa.Dto.TipoDocumento.TipoDocumentoDTO;
import com.isi.desa.Model.Entities.Tipodocumento.TipoDocumento;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TipoDocumentoDAO implements ITipoDocumentoDAO {

    private static final String RES_DIR   = "jsonDataBase";
    private static final String JSON_FILE = "tipoDocumento.json";

    private final ObjectMapper mapper = new ObjectMapper();

    // ===== Helpers de ruta =====
    private File getJsonFileForRead() {
        try {
            String resourcePath = RES_DIR + "/" + JSON_FILE;
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            URL url = cl.getResource(resourcePath);
            if (url != null && !"jar".equalsIgnoreCase(url.getProtocol())) {
                File f = Paths.get(url.toURI()).toFile();
                if (f.exists()) return f;
            }
            return getJsonFileForWrite();
        } catch (Exception e) {
            return getJsonFileForWrite();
        }
    }

    private File getJsonFileForWrite() {
        File dev = Paths.get("src","main","resources",RES_DIR,JSON_FILE).toFile();
        try {
            ensureFile(dev);
            return dev;
        } catch (Exception ignore) {
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
    private List<TipoDocumento> leerTipos() {
        File file = getJsonFileForRead();
        try {
            if (file.length() == 0) return new ArrayList<>();
            return mapper.readValue(file, new TypeReference<List<TipoDocumento>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Error al leer " + JSON_FILE, e);
        }
    }

    private void guardarTipos(List<TipoDocumento> tipos) {
        try {
            File file = getJsonFileForWrite();
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, tipos);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar " + JSON_FILE, e);
        }
    }

    private TipoDocumento dtoToEntity(TipoDocumentoDTO dto) {
        return new TipoDocumento(dto.tipoDocumento, dto.descripcion);
    }

    // ===== Implementacion ITipoDocumentoDAO =====
    @Override
    public TipoDocumento crear(TipoDocumentoDTO dto) {
        List<TipoDocumento> tipos = leerTipos();

        boolean existe = tipos.stream().anyMatch(t -> t.getTipoDocumento().equals(dto.tipoDocumento));
        if (existe) throw new RuntimeException("Ya existe un tipo de documento con ID " + dto.tipoDocumento);

        TipoDocumento nuevo = dtoToEntity(dto);
        tipos.add(nuevo);
        guardarTipos(tipos);
        return nuevo;
    }

    @Override
    public TipoDocumento obtener(String id) {
        List<TipoDocumento> tipos = leerTipos();
        return tipos.stream()
                .filter(t -> t.getTipoDocumento().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No se encontro tipo de documento con ID: " + id));
    }

    @Override
    public TipoDocumento modificar(TipoDocumentoDTO dto) {
        List<TipoDocumento> tipos = leerTipos();

        Optional<TipoDocumento> existente = tipos.stream()
                .filter(t -> t.getTipoDocumento().equals(dto.tipoDocumento))
                .findFirst();

        if (existente.isEmpty()) {
            throw new RuntimeException("No se encontro tipo de documento con ID: " + dto.tipoDocumento);
        }

        TipoDocumento actualizado = existente.get();
        actualizado.setDescripcion(dto.descripcion);

        int index = tipos.indexOf(existente.get());
        tipos.set(index, actualizado);

        guardarTipos(tipos);
        return actualizado;
    }

    @Override
    public TipoDocumento eliminar(TipoDocumentoDTO dto) {
        List<TipoDocumento> tipos = leerTipos();

        Optional<TipoDocumento> existente = tipos.stream()
                .filter(t -> t.getTipoDocumento().equals(dto.tipoDocumento))
                .findFirst();

        if (existente.isEmpty()) {
            throw new RuntimeException("No se encontro tipo de documento para eliminar: " + dto.tipoDocumento);
        }

        tipos.remove(existente.get());
        guardarTipos(tipos);
        return existente.get();
    }
}
