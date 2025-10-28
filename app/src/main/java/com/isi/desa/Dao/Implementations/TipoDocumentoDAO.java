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

    private static final String JSON_RESOURCE = "jsonDataBase/tipoDocumento.json";
    private final ObjectMapper mapper = new ObjectMapper();

    private File getJsonFile() {
        try {
            java.net.URL resourceUrl = getClass().getClassLoader().getResource(JSON_RESOURCE);
            if (resourceUrl == null) {
                // Si no existe, lo creamos en la carpeta de recursos de trabajo
                File file = new File("src/main/resources/" + JSON_RESOURCE);
                file.getParentFile().mkdirs();
                file.createNewFile();
                return file;
            }
            return new File(resourceUrl.toURI());
        } catch (Exception e) {
            throw new RuntimeException("No se pudo acceder al archivo de estadias.", e);
        }
    }

    private List<TipoDocumento> leerTipos() {
        File file = getJsonFile();
        if (!file.exists()) {
            System.out.println("El archivo tipoDocumento.json no existe, creando nuevo...");
            return new ArrayList<>();
        }
        try {
            if (file.length() == 0) {
                return new ArrayList<>();
            }
            return mapper.readValue(file, new TypeReference<List<TipoDocumento>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("El archivo tipoDocumento.json esta corrupto o tiene formato invalido.", e);
        } catch (IOException e) {
            throw new RuntimeException("Error al leer tipoDocumento.json.", e);
        }
    }

    private void guardarTipos(List<TipoDocumento> tipos) {
        try {
            File file = getJsonFile();
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, tipos);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar tipoDocumento.json.", e);
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
            throw new RuntimeException("Ya existe un tipo de documento con ID " + dto.tipoDocumento);
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

        // Actualizamos la descripcion
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
            throw new RuntimeException("No se encontro tipo de documento para eliminar: " + dto.tipoDocumento);
        }

        tipos.remove(existente.get());
        guardarTipos(tipos);
        return existente.get();
    }
}
