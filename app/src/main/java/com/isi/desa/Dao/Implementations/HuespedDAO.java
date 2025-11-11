package com.isi.desa.Dao.Implementations;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.isi.desa.Dao.Interfaces.IHuespedDAO;
import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Exceptions.Huesped.HuespedConEstadiaAsociadasException;
import com.isi.desa.Exceptions.Huesped.HuespedDuplicadoException;
import com.isi.desa.Exceptions.Huesped.HuespedNotFoundException;
import com.isi.desa.Model.Entities.Direccion.Direccion;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Utils.Mappers.HuespedMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

@Service
public class HuespedDAO implements IHuespedDAO {

    private final ObjectMapper mapper;
    private static DireccionDAO direccionDAO;
    private static TipoDocumentoDAO tipoDocumentoDAO;

    public HuespedDAO() {
        this.mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); //Permitir leer/escribir LocalDate correctamente
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); //Evitar escribir fechas como timestamps (numeros)
        direccionDAO = new DireccionDAO();
        tipoDocumentoDAO = new TipoDocumentoDAO();
    }
// CONFIGURACIÓN DEL ARCHIVO JSON ÚNICO (modo desarrollo y ejecución directa)
    private static final String RES_DIR = "app/src/main/resources/jsonDataBase";
    private static final String JSON_FILE = "huesped.json";

    private File getJsonFile() {
        try {
            File dir = new File(RES_DIR);
            if (!dir.exists()) dir.mkdirs();

            File file = new File(dir, JSON_FILE);
            if (!file.exists()) file.createNewFile();
            return file;
        } catch (IOException e) {
            throw new RuntimeException("No se pudo acceder o crear el archivo JSON de huespedes.", e);
        }
    }

    // === Lee todos los huespedes desde el archivo JSON ===
    public List<Huesped> leerHuespedes() {
        File file = getJsonFile();
        try {
            if (file.length() == 0) return new ArrayList<>();
            return mapper.readValue(file, new TypeReference<List<Huesped>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo JSON de huespedes: " + file.getAbsolutePath(), e);
        }
    }

    // === Guarda la lista completa de huespedes en el archivo JSON. ===

    private boolean guardarHuespedes(List<Huesped> huespedes) {
        File file = getJsonFile();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, huespedes);
            return true;
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo JSON de huespedes: " + file.getAbsolutePath(), e);
        }
    }


    @Override
    public Huesped crear(HuespedDTO huesped) throws HuespedDuplicadoException {
        List<Huesped> huespedes = leerHuespedes();

        // Generar ID incremental si no viene (HU-###)
        if (huesped.idHuesped == null || huesped.idHuesped.isBlank()) {
            int max = 0;
            for (Huesped d : huespedes) {
                String id = d.getIdHuesped(); // p.ej.: "HU-015"
                if (id != null && id.startsWith("HU-")) {
                    try {
                        int n = Integer.parseInt(id.substring(3));
                        if (n > max) max = n;
                    } catch (NumberFormatException ignored) {}
                }
            }
            huesped.idHuesped = String.format("HU-%03d", max + 1);
        }

        boolean existe = huespedes.stream()
                .anyMatch(d -> d.getIdHuesped().equalsIgnoreCase(huesped.idHuesped));
        if (existe) {
            throw new RuntimeException("Ya existe una huesped con el ID: " + huesped.idHuesped);
        }

        Huesped nuevo = HuespedMapper.dtoToEntity(huesped);
        nuevo.setEliminado(false);
        huespedes.add(nuevo);
        guardarHuespedes(huespedes);
        return nuevo;
    }

    @Override
    public Huesped modificar(HuespedDTO dto) {
        List<Huesped> huespedes = leerHuespedes();

        // Buscar huésped por ID
        int idx = -1;
        for (int i = 0; i < huespedes.size(); i++) {
            Huesped h = huespedes.get(i);
            if (!h.isEliminado() && h.getIdHuesped() != null &&
                    h.getIdHuesped().equalsIgnoreCase(dto.idHuesped)) {
                idx = i;
                break;
            }
        }

        if (idx == -1) {
            throw new HuespedNotFoundException(
                    "No se encontró huésped con ID: " + dto.idHuesped);
        }

        Huesped existente = huespedes.get(idx);

        // Crear el huésped actualizado a partir del DTO
        Huesped actualizado = HuespedMapper.dtoToEntity(dto);

        // Preservar campos internos
        actualizado.setIdsEstadias(existente.getIdsEstadias());
        actualizado.setEliminado(existente.isEliminado());

        // Reemplazar el huésped en la lista (pisando el anterior)
        huespedes.set(idx, actualizado);

        // Actualizar datos relacionados
        if (dto.direccion != null)
            direccionDAO.modificar(dto.direccion);
        if (dto.tipoDocumento != null)
            tipoDocumentoDAO.modificar(dto.tipoDocumento);

        // Guardar lista completa en el mismo archivo
        boolean ok = guardarHuespedes(huespedes);
        if (!ok) {
            throw new RuntimeException("Error al guardar los cambios del huésped con ID: " + dto.idHuesped);
        }
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

    // === Agrega un ID de estadia al huesped correspondiente ===
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

    // === Devuelve la lista de IDs de estadias de un huesped. ===
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
