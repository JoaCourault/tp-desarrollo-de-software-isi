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
import com.isi.desa.Dao.Interfaces.ITipoDocumentoDAO;
import com.isi.desa.Dao.Interfaces.IDireccionDAO;
import com.isi.desa.Dao.Implementations.TipoDocumentoDAO;
import com.isi.desa.Dao.Implementations.DireccionDAO;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HuespedDAO implements IHuespedDAO {

    private static final String JSON_RESOURCE = "jsonDataBase/huesped.json";
    private final ObjectMapper mapper;
    private final ITipoDocumentoDAO tipoDocDAO = new TipoDocumentoDAO();
    private final IDireccionDAO direccionDAO = new DireccionDAO();

    public HuespedDAO() {
        this.mapper = new ObjectMapper();
        //Permitir leer/escribir LocalDate correctamente
        mapper.registerModule(new JavaTimeModule());
        //Evitar escribir fechas como timestamps (números)
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private File getJsonFile() {
        try {
            // 1) Carpeta de datos externa (default ./data)
            String baseDir = System.getProperty("app.dataDir", "data");
            File file = new File(baseDir, JSON_RESOURCE);
            file.getParentFile().mkdirs();

            // 2) Crear si no existe
            if (!file.exists()) {
                file.createNewFile();
            }

            // 3) Si está vacío, copiar seed desde el classpath (si existe)
            if (file.length() == 0) {
                try (var in = getClass().getClassLoader().getResourceAsStream(JSON_RESOURCE)) {
                    if (in != null) {
                        java.nio.file.Files.copy(in, file.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }

            return file;
        } catch (Exception e) {
            throw new RuntimeException("No se pudo acceder al archivo de huéspedes: " + JSON_RESOURCE, e);
        }
    }


    /**
     * Lee el archivo JSON completo y devuelve todos los huéspedes.
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
            throw new RuntimeException(" El archivo de huéspedes está corrupto o tiene formato inválido.", e);
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo de huéspedes.", e);
        }
    }

    @Override
    public boolean existePorTipoYNumDocExceptoId(String tipoDoc, String numDoc, String idHuesped) {
        return leerHuespedes().stream()
                .anyMatch(h ->
                        h.getTipoDocumento().getTipoDocumento().equalsIgnoreCase(tipoDoc) &&
                                h.getNumDoc().equalsIgnoreCase(numDoc) &&
                                !h.getIdHuesped().equalsIgnoreCase(idHuesped) // <– excluye a sí mismo
                );
    }


    /**
     * Guarda toda la lista de huéspedes en el archivo JSON.
     */
    private void guardarHuespedes(List<Huesped> huespedes) {
        try {
            File file = getJsonFile();
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, huespedes);
        } catch (IOException e) {
            throw new RuntimeException(" Error al guardar huéspedes en el archivo JSON.", e);
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

        // Tipo de documento: resolver por ID usando el DAO
        if (dto.tipoDocumento != null && dto.tipoDocumento.tipoDocumento != null) {
            h.setTipoDocumento(tipoDocDAO.obtener(dto.tipoDocumento.tipoDocumento));
        }

        // Dirección: resolver por ID usando el DAO
        if (dto.direccion != null && dto.direccion.id != null) {
            h.setDireccion(direccionDAO.obtener(dto.direccion));
        }

        return h;
    }



    @Override
    public Huesped crear(HuespedDTO dto) {
        List<Huesped> list = leerHuespedes();
        if (list.stream().anyMatch(h -> dto.numDoc != null && dto.numDoc.equals(h.getNumDoc())))
            throw new RuntimeException("Ya existe un huésped con DNI: " + dto.numDoc);
        if (list.stream().anyMatch(h -> dto.idHuesped != null && dto.idHuesped.equals(h.getIdHuesped())))
            throw new RuntimeException("Ya existe un huésped con ID: " + dto.idHuesped);
        Huesped nuevo = dtoToEntity(dto);
        list.add(nuevo);
        guardarHuespedes(list);
        return nuevo;
    }

    @Override
    public Huesped modificar(HuespedDTO huesped) {
        List<Huesped> huespedes = leerHuespedes();

        Optional<Huesped> existente = huespedes.stream()
                .filter(h -> h.getNumDoc().equals(huesped.numDoc))
                .findFirst();

        if (existente.isEmpty()) {
            throw new RuntimeException("No se encontró huésped con documento: " + huesped.numDoc);
        }

        Huesped actualizado = dtoToEntity(huesped);
        int index = huespedes.indexOf(existente.get());
        huespedes.set(index, actualizado);

        guardarHuespedes(huespedes);
        return actualizado;
    }

    @Override
    public Huesped eliminar(HuespedDTO dto) {
        List<Huesped> list = leerHuespedes();

        Optional<Huesped> existente = list.stream()
                .filter(h ->
                        (dto.idHuesped != null && dto.idHuesped.equals(h.getIdHuesped())) ||
                                (dto.numDoc != null && dto.numDoc.equals(h.getNumDoc()))
                )
                .findFirst();

        if (existente.isEmpty()) {
            throw new RuntimeException("No se encontró huésped para eliminar: " +
                    (dto.idHuesped != null ? dto.idHuesped : dto.numDoc));
        }

        list.remove(existente.get());
        guardarHuespedes(list);
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
}
