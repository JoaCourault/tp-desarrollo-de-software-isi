package com.isi.desa.Dao.Implementations;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isi.desa.Dao.Interfaces.ITipoDocumentoDAO;
import com.isi.desa.Dto.TipoDocumento.TipoDocumentoDTO;
import com.isi.desa.Model.Entities.Tipodocumento.TipoDocumento;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TipoDocumentoDAO implements ITipoDocumentoDAO {

    private static final String RES_DIR   = "jsonDataBase";
    private static final String JSON_FILE = "tipoDocumento.json";

    private final ObjectMapper mapper = new ObjectMapper();

    // ===== Helpers de ruta (solo build) =====
    private File getJsonFileForRead() {
        try {
            File f1 = Paths.get("app","build","resources","main",RES_DIR,JSON_FILE).toFile();
            if (f1.exists()) return f1;

            File f2 = Paths.get("build","resources","main",RES_DIR,JSON_FILE).toFile();
            if (f2.exists()) return f2;

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
    private List<TipoDocumentoDTO> leerTiposDto() {
        File file = getJsonFileForRead();
        try {
            if (file.length() == 0) return new ArrayList<>();
            return mapper.readValue(file, new TypeReference<List<TipoDocumentoDTO>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Error al leer " + JSON_FILE, e);
        }
    }

    private void guardarTipos(List<TipoDocumentoDTO> tipos) {
        try {
            File file = getJsonFileForWrite();
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, tipos);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar " + JSON_FILE, e);
        }
    }

    private TipoDocumento dtoToEntity(TipoDocumentoDTO dto) {
        return new TipoDocumento(dto.tipoDocumento);
    }

    // ===== Implementacion ITipoDocumentoDAO =====
    @Override
    public TipoDocumento crear(TipoDocumentoDTO dto) {
        List<TipoDocumentoDTO> tipos = leerTiposDto();

        boolean existe = tipos.stream().anyMatch(t -> t.tipoDocumento != null && t.tipoDocumento.equals(dto.tipoDocumento));
        if (existe) throw new RuntimeException("Ya existe un tipo de documento con ID " + dto.tipoDocumento);

        tipos.add(dto);
        guardarTipos(tipos);
        return dtoToEntity(dto);
    }

    @Override
    public TipoDocumento obtener(String id) {
        List<TipoDocumentoDTO> tipos = leerTiposDto();
        Optional<TipoDocumentoDTO> found = tipos.stream()
                .filter(t -> t.tipoDocumento != null && t.tipoDocumento.equals(id))
                .findFirst();
        if (found.isEmpty()) {
            throw new RuntimeException("No se encontro tipo de documento con ID: " + id);
        }
        return new TipoDocumento(found.get().tipoDocumento);
    }

    @Override
    public TipoDocumento modificar(TipoDocumentoDTO dto) {
        List<TipoDocumentoDTO> tipos = leerTiposDto();

        Optional<TipoDocumentoDTO> existente = tipos.stream()
                .filter(t -> t.tipoDocumento != null && t.tipoDocumento.equals(dto.tipoDocumento))
                .findFirst();

        if (existente.isEmpty()) {
            throw new RuntimeException("No se encontro tipo de documento con ID: " + dto.tipoDocumento);
        }

        TipoDocumentoDTO actualizado = existente.get();
        // No hay campo descripcion: el identificador (tipoDocumento) es único y no se modifica aquí.

        int index = tipos.indexOf(existente.get());
        tipos.set(index, actualizado);

        guardarTipos(tipos);
        return dtoToEntity(actualizado);
    }

    @Override
    public TipoDocumento eliminar(TipoDocumentoDTO dto) {
        List<TipoDocumentoDTO> tipos = leerTiposDto();

        Optional<TipoDocumentoDTO> existente = tipos.stream()
                .filter(t -> t.tipoDocumento != null && t.tipoDocumento.equals(dto.tipoDocumento))
                .findFirst();

        if (existente.isEmpty()) {
            throw new RuntimeException("No se encontro tipo de documento para eliminar: " + dto.tipoDocumento);
        }

        tipos.remove(existente.get());
        guardarTipos(tipos);
        return dtoToEntity(existente.get());
    }
}
