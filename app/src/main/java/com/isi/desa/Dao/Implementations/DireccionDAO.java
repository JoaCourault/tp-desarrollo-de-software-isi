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

public class DireccionDAO implements IDireccionDAO {

    // 📌 Ruta del archivo JSON
    private static final String JSON_PATH = "src/main/resources/jsonDataBase/direccion.json";

    // 🔧 Mapper de Jackson para serializar/deserializar
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Lee todas las direcciones desde el archivo JSON.
     */
    private List<Direccion> leerDirecciones() {
        File file = new File(JSON_PATH);

        if (!file.exists()) {
            throw new RuntimeException("❌ No se encontró el archivo de direcciones en la ruta: " + JSON_PATH);
        }

        try {
            if (file.length() == 0) {
                return new ArrayList<>(); // archivo vacío
            }
            return mapper.readValue(file, new TypeReference<List<Direccion>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("⚠️ El archivo de direcciones está corrupto o tiene formato inválido.", e);
        } catch (IOException e) {
            throw new RuntimeException("💥 Error al leer el archivo de direcciones.", e);
        }
    }

    /**
     * Guarda la lista de direcciones en el archivo JSON.
     */
    private void guardarDirecciones(List<Direccion> direcciones) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(JSON_PATH), direcciones);
        } catch (IOException e) {
            if (e.getMessage() != null && e.getMessage().contains("No space left on device")) {
                throw new RuntimeException("💾 Espacio insuficiente en disco para guardar direcciones.", e);
            }
            throw new RuntimeException("💥 Error al guardar direcciones en el archivo JSON.", e);
        }
    }

    /**
     * Convierte un DTO en una entidad Direccion.
     */
    private Direccion dtoToEntity(DireccionDTO dto) {
        Direccion d = new Direccion();
        d.setIdDireccion(dto.id);
        d.setPais(dto.pais);
        d.setProvincia(dto.provincia);
        d.setLocalidad(dto.localidad);
        d.setCp(dto.codigoPostal);
        d.setCalle(dto.calle);
        d.setNumero(dto.numero);
        d.setDepartamento(dto.departamento);
        d.setPiso(dto.piso);
        return d;
    }

    /**
     * Convierte una entidad en un DTO (opcional si necesitás devolver DTOs en el futuro).
     */
    private DireccionDTO entityToDto(Direccion d) {
        DireccionDTO dto = new DireccionDTO();
        dto.id = d.getIdDireccion();
        dto.pais = d.getPais();
        dto.provincia = d.getProvincia();
        dto.localidad = d.getLocalidad();
        dto.codigoPostal = d.getCp();
        dto.calle = d.getCalle();
        dto.numero = d.getNumero();
        dto.departamento = d.getDepartamento();
        dto.piso = d.getPiso();
        return dto;
    }

    // ============================================================
    // 📦 Implementación de la interfaz IDireccionDAO
    // ============================================================

    @Override
    public Direccion crear(DireccionDTO direccion) {
        List<Direccion> direcciones = leerDirecciones();

        boolean existe = direcciones.stream()
                .anyMatch(d -> d.getIdDireccion().equalsIgnoreCase(direccion.id));

        if (existe) {
            throw new RuntimeException("⚠️ Ya existe una dirección con el ID: " + direccion.id);
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
            throw new RuntimeException("❌ No se encontró la dirección con ID: " + direccion.id);
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
            throw new RuntimeException("⚠️ No se encontró la dirección a eliminar: " + direccion.id);
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
                .orElseThrow(() -> new RuntimeException("❌ No se encontró dirección con ID: " + direccion.id));
    }
}
