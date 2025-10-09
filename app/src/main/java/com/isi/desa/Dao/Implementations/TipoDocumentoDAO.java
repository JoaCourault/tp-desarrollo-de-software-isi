package com.isi.desa.Dao.Implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isi.desa.Dao.Interfaces.ITipoDocumentoDAO;
import com.isi.desa.Dto.TipoDocumento.TipoDocumentoDTO;
import com.isi.desa.Model.Entities.Tipodocumento.TipoDocumento;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TipoDocumentoDAO implements ITipoDocumentoDAO {

    private static final String JSON_PATH = "src/main/resources/jsonDataBase/tipoDocumento.json";
    private final ObjectMapper mapper = new ObjectMapper();

    private List<TipoDocumento> leerTipos() {
        File file = new File(JSON_PATH);
        if (!file.exists()) {
            System.out.println("⚠️ El archivo tipoDocumento.json no existe, creando nuevo...");
            return new ArrayList<>();
        }

        try {
            if (file.length() == 0) {
                return new ArrayList<>();
            }
            return mapper.readValue(file, new TypeReference<List<TipoDocumento>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("⚠️ El archivo tipoDocumento.json está corrupto o tiene formato inválido.", e);
        } catch (IOException e) {
            throw new RuntimeException("💥 Error al leer tipoDocumento.json.", e);
        }
    }

    private void guardarTipos(List<TipoDocumento> tipos) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(JSON_PATH), tipos);
        } catch (IOException e) {
            throw new RuntimeException("💥 Error al guardar tipoDocumento.json.", e);
        }
    }

    private TipoDocumento dtoToEntity(TipoDocumentoDTO dto) {
        return new TipoDocumento(dto.tipoDocumento, dto.descripcion);
    }

    @Override
    public TipoDocumento crear(TipoDocumentoDTO dto) {
        List<TipoDocumento> tipos = leerTipos();

        boolean existe = tipos.stream()
                .anyMatch(t -> t.getTipoDocumento().equals(dto.tipoDocumento));

        if (existe) {
            throw new RuntimeException("⚠️ Ya existe un tipo de documento con ID " + dto.tipoDocumento);
        }

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
                .orElseThrow(() -> new RuntimeException("❌ No se encontró tipo de documento con ID: " + id));
    }

    @Override
    public TipoDocumento modificar(TipoDocumentoDTO dto) {
        List<TipoDocumento> tipos = leerTipos();

        Optional<TipoDocumento> existente = tipos.stream()
                .filter(t -> t.getTipoDocumento().equals(dto.tipoDocumento))
                .findFirst();

        if (existente.isEmpty()) {
            throw new RuntimeException("❌ No se encontró tipo de documento con ID: " + dto.tipoDocumento);
        }

        // Actualizamos la descripción
        TipoDocumento actualizado = existente.get();
        actualizado.setDescripcion(dto.descripcion);

        // Reemplazamos en la lista
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
            throw new RuntimeException("⚠️ No se encontró tipo de documento para eliminar: " + dto.tipoDocumento);
        }

        tipos.remove(existente.get());
        guardarTipos(tipos);
        return existente.get();
    }
}
