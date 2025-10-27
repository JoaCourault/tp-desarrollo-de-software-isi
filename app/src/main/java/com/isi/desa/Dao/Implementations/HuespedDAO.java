package com.isi.desa.Dao.Implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.isi.desa.Dao.Interfaces.IHuespedDAO;
import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Utils.Mappers.HuespedMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class HuespedDAO implements IHuespedDAO {

    private static final String JSON_RESOURCE = "jsonDataBase/huesped.json";
    private final ObjectMapper mapper;

    public HuespedDAO() {
        this.mapper = new ObjectMapper();
        // Permitir leer/escribir LocalDate correctamente
        mapper.registerModule(new JavaTimeModule());
        // Evitar escribir fechas como timestamps
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

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
            throw new RuntimeException("No se pudo acceder al archivo de huéspedes.", e);
        }
    }

    /**
     * Lee el archivo JSON completo y devuelve todos los huéspedes.
     */
    public List<Huesped> leerHuespedes() {
        File file = getJsonFile();
        if (!file.exists()) {
            System.out.println("El archivo de huéspedes no existe, creando nuevo...");
            return new ArrayList<>();
        }
        try {
            if (file.length() == 0) {
                return new ArrayList<>();
            }
            return mapper.readValue(file, new TypeReference<List<Huesped>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("El archivo de huéspedes está corrupto o tiene formato inválido.", e);
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo de huéspedes.", e);
        }
    }

    /**
     * Guarda toda la lista de huéspedes en el archivo JSON.
     */
    private void guardarHuespedes(List<Huesped> huespedes) {
        try {
            File file = getJsonFile();
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, huespedes);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar huéspedes en el archivo JSON.", e);
        }
    }

    @Override
    public Huesped crear(HuespedDTO huesped) {
        List<Huesped> huespedes = leerHuespedes();

        boolean existe = huespedes.stream()
                .anyMatch(h -> h.getNumDoc().equalsIgnoreCase(huesped.numDoc));

        if (existe) {
            throw new RuntimeException("Ya existe un huésped con el documento: " + huesped.numDoc);
        }

        Huesped nuevo = HuespedMapper.dtoToEntity(huesped);
        huespedes.add(nuevo);
        guardarHuespedes(huespedes);
        return nuevo;
    }

    @Override
    public Huesped modificar(HuespedDTO huesped) {
        List<Huesped> huespedes = leerHuespedes();

        Optional<Huesped> existente = huespedes.stream()
                .filter(h -> h.getNumDoc().equalsIgnoreCase(huesped.numDoc))
                .findFirst();

        if (existente.isEmpty()) {
            throw new RuntimeException("No se encontró huésped con documento: " + huesped.numDoc);
        }

        Huesped actualizado = HuespedMapper.dtoToEntity(huesped);
        actualizado.setIdsEstadias(existente.get().getIdsEstadias()); // mantener historial de estadías
        int index = huespedes.indexOf(existente.get());
        huespedes.set(index, actualizado);

        guardarHuespedes(huespedes);
        return actualizado;
    }

    @Override
    public Huesped eliminar(HuespedDTO huesped) {
        List<Huesped> huespedes = leerHuespedes();

        Optional<Huesped> existente = huespedes.stream()
                .filter(h -> h.getNumDoc().equalsIgnoreCase(huesped.numDoc))
                .findFirst();

        if (existente.isEmpty()) {
            throw new RuntimeException("No se encontró huésped para eliminar: " + huesped.numDoc);
        }
        Huesped encontrado = existente.get();
        //Verifica si tiene estadías asociadas
        if (encontrado.getIdsEstadias() != null && !encontrado.getIdsEstadias().isEmpty()) {
            throw new RuntimeException("No se puede eliminar el huésped "
                    + encontrado.getNombre() + " " + encontrado.getApellido()
                    + " porque tiene estadías asociadas (" + encontrado.getIdsEstadias().size() + ").");
        }

        huespedes.remove(existente.get());
        guardarHuespedes(huespedes);

        return existente.get();
    }


    @Override
    public Huesped obtenerHuesped(String DNI) {
        List<Huesped> huespedes = leerHuespedes();
        return huespedes.stream()
                .filter(h -> h.getNumDoc().equals(DNI))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No se encontró huésped con DNI: " + DNI));
    }

    /* Agrega un ID de estadía al huésped correspondiente */
    public void agregarEstadiaAHuesped(String idHuesped, String idEstadia) {
        List<Huesped> huespedes = leerHuespedes();

        Optional<Huesped> existente = huespedes.stream()
                .filter(h -> h.getIdHuesped().equalsIgnoreCase(idHuesped))
                .findFirst();

        if (existente.isEmpty()) {
            throw new RuntimeException("No se encontró huésped con ID: " + idHuesped);
        }

        Huesped h = existente.get();
        h.agregarEstadia(idEstadia);
        guardarHuespedes(huespedes);
    }

    public void eliminarEstadiaDeHuesped(String idHuesped, String idEstadia) {
        List<Huesped> huespedes = leerHuespedes();

        Optional<Huesped> existente = huespedes.stream()
                .filter(h -> h.getIdHuesped().equalsIgnoreCase(idHuesped))
                .findFirst();

        if (existente.isEmpty()) {
            throw new RuntimeException("No se encontró huésped con ID: " + idHuesped);
        }

        Huesped h = existente.get();
        h.eliminarEstadia(idEstadia);
        guardarHuespedes(huespedes);
    }

    /**
     * Devuelve la lista de IDs de estadías de un huésped.
     */
    public List<String> obtenerIdsEstadiasDeHuesped(String idHuesped) {
        return leerHuespedes().stream()
                .filter(h -> h.getIdHuesped().equalsIgnoreCase(idHuesped))
                .findFirst()
                .map(Huesped::getIdsEstadias)
                .orElse(Collections.emptyList());
    }
}
