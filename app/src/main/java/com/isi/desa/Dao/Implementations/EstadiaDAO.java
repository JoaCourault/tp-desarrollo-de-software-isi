package com.isi.desa.Dao.Implementations;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.isi.desa.Dao.Interfaces.IEstadiaDAO;
import com.isi.desa.Dto.Estadia.EstadiaDTO;
import com.isi.desa.Model.Entities.Estadia.Estadia;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EstadiaDAO implements IEstadiaDAO {

    private static final String RES_DIR   = "jsonDataBase";
    private static final String JSON_FILE = "estadia.json";

    private final ObjectMapper mapper;

    public EstadiaDAO() {
        this.mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    // Helpers de ruta (solo build)
    private File getJsonFileForRead() {
        try {
            File f1 = Paths.get("app","build","resources","main",RES_DIR,JSON_FILE).toFile();
            if (f1.exists()) return f1;
            File f2 = Paths.get("build","resources","main",RES_DIR,JSON_FILE).toFile();
            if (f2.exists()) return f2;
            throw new RuntimeException("No se encontro el archivo de datos '" + RES_DIR + "/" + JSON_FILE + "' en build. Ejecute el build y reintente.");
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException("Error al localizar el archivo JSON en build: " + e.getMessage(), e);
        }
    }

    private File getJsonFileForWrite() {
        File f1 = Paths.get("app","build","resources","main",RES_DIR,JSON_FILE).toFile();
        if (f1.exists() && f1.isFile()) return f1;
        File f2 = Paths.get("build","resources","main",RES_DIR,JSON_FILE).toFile();
        if (f2.exists() && f2.isFile()) return f2;
        throw new RuntimeException("No se encontro archivo de salida en build para escribir '" + RES_DIR + "/" + JSON_FILE + "'. No se crean carpetas nuevas. Ejecute el build primero.");
    }

    // === IO ===
    public List<Estadia> leerEstadias() {
        File file = getJsonFileForRead();
        try {
            if (file.length() == 0) return new ArrayList<>();
            return mapper.readValue(file, new TypeReference<List<Estadia>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Error al leer " + JSON_FILE, e);
        }
    }

    private void guardarEstadias(List<Estadia> estadias) {
        try {
            File file = getJsonFileForWrite();
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, estadias);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar " + JSON_FILE, e);
        }
    }

    // === Mapeo DTO->Entidad (si no, usa el mapper) ===
    private Estadia dtoToEntity(EstadiaDTO dto) {
        Estadia e = new Estadia();
        e.setIdEstadia(dto.idEstadia);
        e.setValorTotalEstadia(dto.valorTotalEstadia);
        e.setCheckIn(dto.checkIn);
        e.setCheckOut(dto.checkOut);
        e.setCantNoches(dto.cantNoches);
        return e;
    }

    // === Implementacion IEstadiaDAO ===
    @Override
    public Estadia crear(EstadiaDTO estadia) {
        List<Estadia> estadias = leerEstadias();
        boolean existe = estadias.stream()
                .anyMatch(e -> e.getIdEstadia().equalsIgnoreCase(estadia.idEstadia));

        if (existe) throw new RuntimeException("Ya existe una estadia con el ID: " + estadia.idEstadia);

        Estadia nueva = dtoToEntity(estadia);
        estadias.add(nueva);
        guardarEstadias(estadias);
        return nueva;
    }

    @Override
    public Estadia modificar(EstadiaDTO estadia) {
        List<Estadia> estadias = leerEstadias();

        Optional<Estadia> existente = estadias.stream()
                .filter(e -> e.getIdEstadia().equalsIgnoreCase(estadia.idEstadia))
                .findFirst();

        if (existente.isEmpty())
            throw new RuntimeException("No se encontro la estadia con ID: " + estadia.idEstadia);

        Estadia actualizada = dtoToEntity(estadia);
        estadias.set(estadias.indexOf(existente.get()), actualizada);
        guardarEstadias(estadias);
        return actualizada;
    }

    @Override
    public Estadia eliminar(EstadiaDTO estadia) {
        List<Estadia> estadias = leerEstadias();

        boolean eliminado = estadias.removeIf(e -> e.getIdEstadia().equalsIgnoreCase(estadia.idEstadia));
        if (!eliminado)
            throw new RuntimeException("No se encontro la estadia a eliminar: " + estadia.idEstadia);

        guardarEstadias(estadias);
        return dtoToEntity(estadia);
    }

    @Override
    public Estadia obtener(EstadiaDTO estadia) {
        List<Estadia> estadias = leerEstadias();
        return estadias.stream()
                .filter(e -> e.getIdEstadia().equalsIgnoreCase(estadia.idEstadia))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No se encontro estadia con ID: " + estadia.idEstadia));
    }

    @Override
    public List<String> obtenerIdsHuespedesConEstadias(String idHuesped) {
        List<Estadia> estadias = leerEstadias();
        List<String> ids = new ArrayList<>();
        for (Estadia e : estadias) {
            try {
                var field = e.getClass().getDeclaredField("huespedes");
                field.setAccessible(true);
                var huespedes = (List<?>) field.get(e);
                if (huespedes == null) continue;
                for (Object h : huespedes) {
                    var metodoId = h.getClass().getMethod("getIdHuesped");
                    String idActual = (String) metodoId.invoke(h);
                    if (idHuesped.equals(idActual)) ids.add(idActual);
                }
            } catch (NoSuchFieldException nf) {
                continue;
            } catch (Exception ex) {
                continue;
            }
        }
        return ids;
    }
}
