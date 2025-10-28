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
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Utils.Mappers.HuespedMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class HuespedDAO implements IHuespedDAO {

    private static final String JSON_RESOURCE = "jsonDataBase/huesped.json";
    private final ObjectMapper mapper;
    private static DireccionDAO direccionDAO;
    private static TipoDocumentoDAO tipoDocumentoDAO;

    public HuespedDAO() {
        this.mapper = new ObjectMapper();
        //Permitir leer/escribir LocalDate correctamente
        mapper.registerModule(new JavaTimeModule());
        //Evitar escribir fechas como timestamps (numeros)
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        direccionDAO = new DireccionDAO();
        tipoDocumentoDAO = new TipoDocumentoDAO();
    }

    private static final String JSON_FILE = "huesped.json";
    private static final String RES_DIR   = "jsonDataBase";

    // 1) Lectura: intenta primero el archivo editable en src/main/resources (dev)
    // si existe lo usa; si no existe intenta la ruta desde classloader; de lo contrario usa fallback escribible
    private File getJsonFileForRead() {
        try {
            // Preferir archivo dev editable (src/main/resources/jsonDataBase/huesped.json)
            File dev = Paths.get("src", "main", "resources", RES_DIR, JSON_FILE).toFile();
            if (dev.exists()) return dev;

            String resourcePath = RES_DIR + "/" + JSON_FILE;
            var cl = Thread.currentThread().getContextClassLoader();
            var url = cl.getResource(resourcePath);
            if (url != null && !"jar".equalsIgnoreCase(url.getProtocol())) {
                File f = new File(url.toURI());
                if (f.exists()) return f;
            }
            // fallback (tambien sirve si queres forzar lectura local)
            return getJsonFileForWrite();
        } catch (Exception e) {
            return getJsonFileForWrite();
        }
    }

    // 2) Escritura: siempre a src/main/resources/jsonDataBase (dev) o data/jsonDataBase (fallback)
    private File getJsonFileForWrite() {
        try {
            // ruta “dev” (IDE): src/main/resources/jsonDataBase/huesped.json
            File dev = Paths.get("src","main","resources",RES_DIR,JSON_FILE).toFile();
            ensureFile(dev);
            return dev;
        } catch (Exception ignore) {
            // fallback para ejecucion empaquetada (JAR): ./data/jsonDataBase/huesped.json
            File external = Paths.get("data",RES_DIR,JSON_FILE).toFile();
            try { ensureFile(external); } catch (Exception ex) {
                throw new RuntimeException("No se pudo crear archivo JSON: " + external.getAbsolutePath(), ex);
            }
            return external;
        }
    }

    private void ensureFile(File f) throws Exception {
        File p = f.getParentFile();
        if (p != null && !p.exists()) p.mkdirs();
        if (!f.exists()) f.createNewFile();
    }


    private String nombreArchivo() {
        // Ejemplo para DireccionDAO:
        return "direccion.json";
    }


    /**
     * Lee el archivo JSON completo y devuelve todos los huespedes.
     */
    public List<Huesped> leerHuespedes() {
        File file = getJsonFileForRead();
        try {
            if (file.length() == 0) return new ArrayList<>();
            return mapper.readValue(file, new TypeReference<List<Huesped>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Error al leer " + JSON_FILE, e);
        }
    }

    private boolean guardarHuespedes(List<Huesped> huespedes) {
        try {
            File file = getJsonFileForWrite();
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, huespedes);
            return true;
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar " + JSON_FILE, e);
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
    public Huesped modificar(HuespedDTO dto) {
        List<Huesped> huespedes = leerHuespedes();

        // 1) buscar SIEMPRE por ID de huesped
        Optional<Huesped> existenteOpt = huespedes.stream()
                .filter(h -> !h.isEliminado())
                .filter(h -> h.getIdHuesped() != null
                        && h.getIdHuesped().equalsIgnoreCase(dto.idHuesped))
                .findFirst();

        if (existenteOpt.isEmpty()) {
            throw new HuespedNotFoundException(
                    "No se encontro huesped con ID: " + dto.idHuesped);
        }

        Huesped existente = existenteOpt.get();

        // 2) mapear lo nuevo manteniendo campos internos
        Huesped actualizado = HuespedMapper.dtoToEntity(dto);

        // 3) reemplazar en la lista por indice
        int idx = huespedes.indexOf(existente);
        huespedes.set(idx, actualizado);

        direccionDAO.modificar(dto.direccion); // actualizar direccion tambien
        tipoDocumentoDAO.modificar(dto.tipoDocumento);
        boolean ok = guardarHuespedes(huespedes);
        if(!ok) {
            throw new RuntimeException("Error al guardar los cambios del huesped con ID: " + dto.idHuesped);
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
