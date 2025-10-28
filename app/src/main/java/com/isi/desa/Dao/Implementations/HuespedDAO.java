package com.isi.desa.Dao.Implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.isi.desa.Dao.Interfaces.IHuespedDAO;
import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Exceptions.HuespedConEstadiaAsociadasException;
import com.isi.desa.Exceptions.HuespedDuplicadoException;
import com.isi.desa.Exceptions.HuespedNotFoundException;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
        //Permitir leer/escribir LocalDate correctamente
        mapper.registerModule(new JavaTimeModule());
        //Evitar escribir fechas como timestamps (numeros)
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

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

    // Helper: si hoy usás "jsonDataBase/direccion.json", podés partirlo o
// directamente setear en cada DAO:
    private String nombreArchivo() {
        // Ejemplo para DireccionDAO:
        return "direccion.json";
    }


    /**
     * Lee el archivo JSON completo y devuelve todos los huespedes.
     */
    public List<Huesped> leerHuespedes() {
        File file = getJsonFile();
        if (!file.exists()) {
            System.out.println("El archivo de huespedes no existe, creando nuevo...");
            return new ArrayList<>();
        }
        try {
            if (file.length() == 0) {
                return new ArrayList<>();
            }
            return mapper.readValue(file, new TypeReference<List<Huesped>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(" El archivo de huespedes esta corrupto o tiene formato invalido.", e);
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo de huespedes.", e);
        }
    }

    /**
     * Guarda toda la lista de huespedes en el archivo JSON.
     */
    private void guardarHuespedes(List<Huesped> huespedes) {
        try {
            File file = getJsonFile();
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, huespedes);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar huespedes en el archivo JSON.", e);
        }
    }

    @Override
    public Huesped crear(HuespedDTO huesped) throws HuespedDuplicadoException {
        List<Huesped> huespedes = leerHuespedes();

        boolean existe = huespedes.stream()
                .filter(h -> !h.isEliminado())
                .anyMatch(h -> h.getNumDoc() != null && h.getNumDoc().equalsIgnoreCase(huesped.numDoc));

        if (existe) {
            throw new HuespedDuplicadoException("Ya existe un huesped con el documento: " + huesped.numDoc);
        }

        Huesped nuevo = HuespedMapper.dtoToEntity(huesped);
        nuevo.setEliminado(false);
        huespedes.add(nuevo);
        guardarHuespedes(huespedes);
        return nuevo;
    }

    @Override
    public Huesped modificar(HuespedDTO huesped) {
        List<Huesped> huespedes = leerHuespedes();

        Optional<Huesped> existente = huespedes.stream()
                .filter(h -> !h.isEliminado())
                .filter(h -> h.getNumDoc() != null && h.getNumDoc().equalsIgnoreCase(huesped.numDoc))
                .findFirst();

        if (existente.isEmpty()) {
            throw new HuespedNotFoundException("No se encontro huesped con documento: " + huesped.numDoc);
        }

        Huesped actualizado = HuespedMapper.dtoToEntity(huesped);
        // mantener historial de estadias y estado eliminado
        actualizado.setIdsEstadias(existente.get().getIdsEstadias());
        actualizado.setEliminado(existente.get().isEliminado());
        int index = huespedes.indexOf(existente.get());
        huespedes.set(index, actualizado);

        guardarHuespedes(huespedes);
        return actualizado;
    }

    @Override
    public Huesped eliminar(String idHuesped) {
        List<Huesped> huespedes = leerHuespedes();

        Optional<Huesped> existente = huespedes.stream()
                .filter(h -> !h.isEliminado())
                .filter(h -> h.getIdHuesped() != null && h.getIdHuesped().equalsIgnoreCase(idHuesped))
                .findFirst();

        if (existente.isEmpty()) {
            throw new HuespedNotFoundException("No se encontro huesped con ID: " + idHuesped);
        }

        Huesped h = existente.get();
        // si tiene estadias activas podriamos lanzar excepcion segun la logica del negocio
        if (h.getIdsEstadias() != null && !h.getIdsEstadias().isEmpty()) {
            throw new HuespedConEstadiaAsociadasException("El huesped tiene estadias asociadas y no puede eliminarse.");
        }

        h.setEliminado(true);
        int index = huespedes.indexOf(h);
        huespedes.set(index, h);
        guardarHuespedes(huespedes);

        return h;
    }


    @Override
    public Huesped obtenerHuesped(String DNI) {
        List<Huesped> huespedes = leerHuespedes();
        return huespedes.stream()
                .filter(h -> !h.isEliminado())
                .filter(h -> h.getNumDoc() != null && h.getNumDoc().equals(DNI))
                .findFirst()
                .orElseThrow(() -> new HuespedNotFoundException("No se encontro huesped con DNI: " + DNI));
    }

    /* Agrega un ID de estadia al huesped correspondiente */
    public void agregarEstadiaAHuesped(String idHuesped, String idEstadia) {
        List<Huesped> huespedes = leerHuespedes();

        Optional<Huesped> existente = huespedes.stream()
                .filter(h -> !h.isEliminado())
                .filter(h -> h.getIdHuesped() != null && h.getIdHuesped().equalsIgnoreCase(idHuesped))
                .findFirst();

        if (existente.isEmpty()) {
            throw new HuespedNotFoundException("No se encontro huesped con ID: " + idHuesped);
        }

        Huesped h = existente.get();
        h.agregarEstadia(idEstadia);
        guardarHuespedes(huespedes);
    }

    public void eliminarEstadiaDeHuesped(String idHuesped, String idEstadia) {
        List<Huesped> huespedes = leerHuespedes();

        Optional<Huesped> existente = huespedes.stream()
                .filter(h -> !h.isEliminado())
                .filter(h -> h.getIdHuesped() != null && h.getIdHuesped().equalsIgnoreCase(idHuesped))
                .findFirst();

        if (existente.isEmpty()) {
            throw new HuespedNotFoundException("No se encontro huesped con ID: " + idHuesped);
        }

        Huesped h = existente.get();
        h.eliminarEstadia(idEstadia);
        guardarHuespedes(huespedes);
    }

    /**
     * Devuelve la lista de IDs de estadias de un huesped.
     */
    public List<String> obtenerIdsEstadiasDeHuesped(String idHuesped) {
        return leerHuespedes().stream()
                .filter(h -> !h.isEliminado())
                .filter(h -> h.getIdHuesped() != null && h.getIdHuesped().equalsIgnoreCase(idHuesped))
                .findFirst()
                .map(Huesped::getIdsEstadias)
                .orElse(Collections.emptyList());
    }

    @Override
    public Huesped getById(String id) {
        List<Huesped> huespedes = leerHuespedes();

        return huespedes.stream()
                .filter(h -> !h.isEliminado())
                .filter(h -> h.getIdHuesped() != null && h.getIdHuesped().equalsIgnoreCase(id))
                .findFirst()
                .orElseThrow(() -> new HuespedNotFoundException("No se encontro huesped con ID: " + id));
    }
}
