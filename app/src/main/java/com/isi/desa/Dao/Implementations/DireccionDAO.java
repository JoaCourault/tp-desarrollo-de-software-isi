package com.isi.desa.Dao.Implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isi.desa.Dao.Interfaces.IDireccionDAO;
import com.isi.desa.Dto.Direccion.DireccionDTO;
import com.isi.desa.Model.Entities.Direccion.Direccion;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.isi.desa.Utils.Mappers.DireccionMapper.dtoToEntity;

public class DireccionDAO implements IDireccionDAO {

    //Ruta del archivo JSON
    private static final String JSON_RESOURCE = "jsonDataBase/direccion.json";
    private final ObjectMapper mapper = new ObjectMapper();

    private File getJsonFile() {
        try {
            java.net.URL resourceUrl = getClass().getClassLoader().getResource(JSON_RESOURCE);
            if (resourceUrl == null) {
                File file = new File("src/main/resources/" + JSON_RESOURCE);
                file.getParentFile().mkdirs();
                file.createNewFile();
                return file;
            }
            return new File(resourceUrl.toURI());
        } catch (Exception e) {
            throw new RuntimeException("No se pudo acceder al archivo de direcciones.", e);
        }
    }

    /**
     * Lee todas las direcciones desde el archivo JSON.
     */
    private List<Direccion> leerDirecciones() {
        File file = getJsonFile();
        if (!file.exists()) {
            throw new RuntimeException("No se encontro el archivo de direcciones en la ruta: " + JSON_RESOURCE);
        }
        try {
            if (file.length() == 0) {
                return new ArrayList<>(); // archivo vacio
            }
            return mapper.readValue(file, new TypeReference<List<Direccion>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("El archivo de direcciones esta corrupto o tiene formato invalido.", e);
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo de direcciones.", e);
        }
    }

    /**
     * Guarda la lista de direcciones en el archivo JSON.
     */
    private void guardarDirecciones(List<Direccion> direcciones) {
        try {
            File file = getJsonFile();
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, direcciones);
        } catch (IOException e) {
            if (e.getMessage() != null && e.getMessage().contains("No space left on device")) {
                throw new RuntimeException("Espacio insuficiente en disco para guardar direcciones.", e);
            }
            throw new RuntimeException("Error al guardar direcciones en el archivo JSON.", e);
        }
    }


    // ============================================================
    //  Implementacion de la interfaz IDireccionDAO
    // ============================================================

    @Override
    public Direccion crear(DireccionDTO direccion) {
        List<Direccion> direcciones = leerDirecciones();

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
