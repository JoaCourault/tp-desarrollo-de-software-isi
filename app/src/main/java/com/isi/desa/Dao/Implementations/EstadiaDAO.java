package com.isi.desa.Dao.Implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.isi.desa.Dao.Interfaces.IEstadiaDAO;
import com.isi.desa.Dto.Estadia.EstadiaDTO;
import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Model.Entities.Estadia.Estadia;
import com.isi.desa.Model.Entities.Huesped.Huesped;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.isi.desa.Utils.Mappers.EstadiaMapper.dtoToEntity;

public class EstadiaDAO implements IEstadiaDAO {

    private static final String JSON_RESOURCE = "jsonDataBase/estadia.json";
    private final ObjectMapper mapper;

    public EstadiaDAO() {
        this.mapper = new ObjectMapper();
        //Permitir leer/escribir LocalDate correctamente
        mapper.registerModule(new JavaTimeModule());
        //Evitar escribir fechas como timestamps (números)
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
            throw new RuntimeException("No se pudo acceder al archivo de estadías.", e);
        }
    }


    /**
     * Lee todas las estadías desde el archivo JSON.
     */

    public List<Estadia> leerEstadias() {
        File file = getJsonFile();
        if (!file.exists()) {
            System.out.println(" El archivo de estadias no existe, creando nuevo...");
            return new ArrayList<>();
        }
        try {
            if (file.length() == 0) {
                return new ArrayList<>();
            }
            return mapper.readValue(file, new TypeReference<List<Estadia>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(" El archivo de estadias está corrupto o tiene formato inválido.", e);
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo de estadias.", e);
        }
    }

    /**
     * Guarda la lista de estadías en el archivo JSON.
     */

    private void guardarEstadias(List<Estadia> estadias) {
        try {
            File file = getJsonFile();
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, estadias);
        } catch (IOException e) {
            throw new RuntimeException(" Error al guardar estadia en el archivo JSON.", e);
        }
    }

    /**
     * Convierte un DTO a entidad.
     */
    private Estadia dtoToEntity(EstadiaDTO dto) {
        Estadia e = new Estadia();
        e.setIdEstadia(dto.idEstadia);
        e.setValorTotalEstadia(dto.valorTotalEstadia);
        e.setCheckIn(dto.checkIn);
        e.setCheckOut(dto.checkOut);
        e.setCantNoches(dto.cantNoches);
        return e;
    }

    @Override

    public Estadia crear(EstadiaDTO estadia) {
        List<Estadia> estadias = leerEstadias();
        boolean existe = estadias.stream()
                .anyMatch(e -> e.getIdEstadia().equalsIgnoreCase(estadia.idEstadia));

        if (existe) {
            throw new RuntimeException("Ya existe una estadía con el ID: " + estadia.idEstadia);
        }

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

        if (existente.isEmpty()) {
            throw new RuntimeException("No se encontró la estadía con ID: " + estadia.idEstadia);
        }

        Estadia actualizada = dtoToEntity(estadia);
        estadias.set(estadias.indexOf(existente.get()), actualizada);
        guardarEstadias(estadias);
        return actualizada;
    }

    @Override
    public Estadia eliminar(EstadiaDTO estadia) {
        List<Estadia> estadias = leerEstadias();

        boolean eliminado = estadias.removeIf(e -> e.getIdEstadia().equalsIgnoreCase(estadia.idEstadia));

        if (!eliminado) {
            throw new RuntimeException("No se encontró la estadía a eliminar: " + estadia.idEstadia);
        }

        guardarEstadias(estadias);
        return dtoToEntity(estadia);
    }

    @Override
    public Estadia obtener(EstadiaDTO estadia) {
        List<Estadia> estadias = leerEstadias();

        return estadias.stream()
                .filter(e -> e.getIdEstadia().equalsIgnoreCase(estadia.idEstadia))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No se encontró estadía con ID: " + estadia.idEstadia));
    }

    /**
     * Verifica si un huésped ha tenido alguna estadía.
     * Supone que EstadiaDTO o Estadia tiene una lista de huéspedes asociados.
     * Si aún no existe esa relación en la entidad, este metodo se actualizará cuando la agregues.
     */
    @Override
    public List<String> obtenerIdsHuespedesConEstadias(String idHuesped) {
        List<Estadia> estadias = leerEstadias();

        List<String> idsHuespedes = new ArrayList<>();

        for (Estadia e : estadias) {
            try {
                // Obtener el campo "huespedes" de la clase Estadia (debe ser List<Huesped>)
                var field = e.getClass().getDeclaredField("huespedes");
                field.setAccessible(true);
                var huespedes = (List<?>) field.get(e);

                if (huespedes == null) continue;

                for (Object h : huespedes) {
                    var metodoId = h.getClass().getMethod("getIdHuesped");
                    String idActual = (String) metodoId.invoke(h);
                    if (idActual != null && idActual.equals(idHuesped)) {
                        idsHuespedes.add(idActual);
                    }
                }

            } catch (NoSuchFieldException nf) {
                // Si aún no existe la relación explícita, continuar
                continue;
            } catch (Exception ex) {
                // Cualquier otro error, continuar con la siguiente estadía
                continue;
            }
        }

        return idsHuespedes;
    }

}
