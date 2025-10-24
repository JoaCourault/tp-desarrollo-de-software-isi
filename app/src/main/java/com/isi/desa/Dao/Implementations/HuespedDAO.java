package com.isi.desa.Dao.Implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isi.desa.Dao.Interfaces.IHuespedDAO;
import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.isi.desa.Utils.Mappers.HuespedMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
            throw new RuntimeException("No se pudo acceder al archivo de huespedes.", e);
        }
    }

    /**
     * Lee el archivo JSON completo y devuelve todos los huespedes.
     */
    public List<Huesped> leerHuespedes() {
        File file = getJsonFile();
        if (!file.exists()) {
            System.out.println(" El archivo de huespedes no existe, creando nuevo...");
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
            throw new RuntimeException(" Error al guardar huespedes en el archivo JSON.", e);
        }
    }

    /**
     * Convierte un DTO a entidad.
     */
    private Huesped dtoToEntity(HuespedDTO dto) {
        Huesped h = new Huesped();
        h.setIdHuesped(dto.idHuesped);
        h.setNombre(dto.nombre);
        h.setApellido(dto.apellido);
        h.setNumDoc(dto.numDoc);
        h.setPosicionIva(dto.posicionIva);
        h.setCuit(dto.cuit);
        h.setFechaNacimiento(dto.fechaNacimiento);
        h.setTelefono(dto.telefono);
        h.setEmail(dto.email);
        h.setOcupacion(dto.ocupacion);
        h.setNacionalidad(dto.nacionalidad);
        return h;
    }

    @Override
    public Huesped crear(HuespedDTO huesped) {
        List<Huesped> huespedes = leerHuespedes(); // se leen todos los huespedes existentes

        Huesped nuevo = dtoToEntity(huesped);
        huespedes.add(nuevo); //  agregamos a la lista existente
        guardarHuespedes(huespedes); //  sobrescribimos con la lista actualizada

        return nuevo;
    }

    @Override
    public Huesped modificar(HuespedDTO huesped) {
        List<Huesped> huespedes = leerHuespedes();

        Optional<Huesped> existente = huespedes.stream()
                .filter(h -> h.getNumDoc().equals(huesped.numDoc))
                .findFirst();

        if (existente.isEmpty()) {
            throw new RuntimeException("No se encontro huesped con documento: " + huesped.numDoc);
        }

        Huesped actualizado = dtoToEntity(huesped);
        int index = huespedes.indexOf(existente.get());
        huespedes.set(index, actualizado);

        guardarHuespedes(huespedes);
        return actualizado;
    }

    @Override
    public Huesped eliminar(HuespedDTO huesped) {
        List<Huesped> huespedes = leerHuespedes();

        Optional<Huesped> existente = huespedes.stream()
                .filter(h -> h.getNumDoc().equals(huesped.numDoc))
                .findFirst();

        if (existente.isEmpty()) {
            throw new RuntimeException(" No se encontro huesped para eliminar: " + huesped.numDoc);
        }

        huespedes.remove(existente.get()); //elimina solo ese huesped
        guardarHuespedes(huespedes); //guarda los demas intactos

        return existente.get();
    }

    @Override
    public Huesped obtenerHuesped(String DNI) {
        List<Huesped> huespedes = leerHuespedes();
        return huespedes.stream()
                .filter(h -> h.getNumDoc().equals(DNI))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No se encontro huesped con DNI: " + DNI));
    }
}
