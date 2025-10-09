package com.isi.desa.Dao.Implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isi.desa.Dao.Interfaces.IHuespedDAO;
import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HuespedDAO implements IHuespedDAO {

    private static final String JSON_PATH = "src/main/resources/jsonDataBase/huesped.json";
    private final ObjectMapper mapper;

    public HuespedDAO() {
        this.mapper = new ObjectMapper();
        // ✅ Permitir leer/escribir LocalDate correctamente
        mapper.registerModule(new JavaTimeModule());
        // ✅ Evitar escribir fechas como timestamps (números)
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * Lee el archivo JSON completo y devuelve todos los huéspedes.
     */
    private List<Huesped> leerHuespedes() {
        File file = new File(JSON_PATH);
        if (!file.exists()) {
            System.out.println("⚠️ El archivo de huéspedes no existe, creando nuevo...");
            return new ArrayList<>();
        }

        try {
            if (file.length() == 0) {
                return new ArrayList<>();
            }
            return mapper.readValue(file, new TypeReference<List<Huesped>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("⚠️ El archivo de huéspedes está corrupto o tiene formato inválido.", e);
        } catch (IOException e) {
            throw new RuntimeException("💥 Error al leer el archivo de huéspedes.", e);
        }
    }

    /**
     * Guarda toda la lista de huéspedes en el archivo JSON.
     */
    private void guardarHuespedes(List<Huesped> huespedes) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(JSON_PATH), huespedes);
        } catch (IOException e) {
            throw new RuntimeException("💥 Error al guardar huéspedes en el archivo JSON.", e);
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
        List<Huesped> huespedes = leerHuespedes(); // ✅ se leen todos los huéspedes existentes

        boolean existe = huespedes.stream()
                .anyMatch(h -> h.getNumDoc().equals(huesped.numDoc));

        if (existe) {
            throw new RuntimeException("⚠️ Ya existe un huésped con el documento " + huesped.numDoc);
        }

        Huesped nuevo = dtoToEntity(huesped);
        huespedes.add(nuevo); // ✅ agregamos a la lista existente
        guardarHuespedes(huespedes); // ✅ sobrescribimos con la lista actualizada

        return nuevo;
    }

    @Override
    public Huesped modificar(HuespedDTO huesped) {
        List<Huesped> huespedes = leerHuespedes();

        Optional<Huesped> existente = huespedes.stream()
                .filter(h -> h.getNumDoc().equals(huesped.numDoc))
                .findFirst();

        if (existente.isEmpty()) {
            throw new RuntimeException("❌ No se encontró huésped con documento: " + huesped.numDoc);
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
            throw new RuntimeException("⚠️ No se encontró huésped para eliminar: " + huesped.numDoc);
        }

        huespedes.remove(existente.get()); // ✅ elimina solo ese huésped
        guardarHuespedes(huespedes); // ✅ guarda los demás intactos

        return existente.get();
    }

    @Override
    public Huesped obtenerHuesped(String DNI) {
        List<Huesped> huespedes = leerHuespedes();
        return huespedes.stream()
                .filter(h -> h.getNumDoc().equals(DNI))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("❌ No se encontró huésped con DNI: " + DNI));
    }
}
