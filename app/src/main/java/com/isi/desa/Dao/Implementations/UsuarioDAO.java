package com.isi.desa.Dao.Implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isi.desa.Dao.Interfaces.IUsuarioDAO;
import com.isi.desa.Dto.Usuario.UsuarioDTO;
import com.isi.desa.Model.Entities.Usuario.Usuario;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioDAO implements IUsuarioDAO {

    private static final String JSON_PATH = "src/main/resources/jsonDataBase/usuario.json";
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Lee todos los usuarios desde el JSON.
     */
    private List<Usuario> leerUsuarios() {
        File file = new File(JSON_PATH);
        if (!file.exists()) {
            System.out.println("El archivo usuario.json no existe, creando nuevo...");
            return new ArrayList<>();
        }

        try {
            if (file.length() == 0) {
                return new ArrayList<>();
            }
            return mapper.readValue(file, new TypeReference<List<Usuario>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("El archivo usuario.json está corrupto o tiene formato inválido.", e);
        } catch (IOException e) {
            throw new RuntimeException("Error al leer usuario.json.", e);
        }
    }

    /**
     * Guarda la lista completa en el archivo JSON.
     */
    private void guardarUsuarios(List<Usuario> usuarios) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(JSON_PATH), usuarios);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar usuario.json.", e);
        }
    }

    /**
     * Convierte un DTO a entidad.
     */
    private Usuario dtoToEntity(UsuarioDTO dto) {
        return new Usuario(dto.idUsuario, dto.contrasenia, dto.nombre, dto.apellido);
    }

    @Override
    public Usuario crear(UsuarioDTO dto) {
        List<Usuario> usuarios = leerUsuarios();

        boolean existe = usuarios.stream()
                .anyMatch(u -> u.getIdUsuario().equals(dto.idUsuario));

        if (existe) {
            throw new RuntimeException("Ya existe un usuario con ID " + dto.idUsuario);
        }

        Usuario nuevo = dtoToEntity(dto);
        usuarios.add(nuevo);
        guardarUsuarios(usuarios);
        return nuevo;
    }

    @Override
    public Usuario modificar(UsuarioDTO dto) {
        List<Usuario> usuarios = leerUsuarios();

        Optional<Usuario> existente = usuarios.stream()
                .filter(u -> u.getIdUsuario().equals(dto.idUsuario))
                .findFirst();

        if (existente.isEmpty()) {
            throw new RuntimeException("No se encontró usuario con ID: " + dto.idUsuario);
        }

        Usuario actualizado = existente.get();
        actualizado.setContrasenia(dto.contrasenia);
        actualizado.setNombre(dto.nombre);
        actualizado.setApellido(dto.apellido);

        int index = usuarios.indexOf(existente.get());
        usuarios.set(index, actualizado);

        guardarUsuarios(usuarios);
        return actualizado;
    }

    @Override
    public Usuario obtener(String idUsuario) {
        List<Usuario> usuarios = leerUsuarios();
        return usuarios.stream()
                .filter(u -> u.getIdUsuario().equals(idUsuario))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No se encontró usuario con ID: " + idUsuario));
    }

    @Override
    public Usuario eliminar(UsuarioDTO dto) {
        List<Usuario> usuarios = leerUsuarios();

        Optional<Usuario> existente = usuarios.stream()
                .filter(u -> u.getIdUsuario().equals(dto.idUsuario))
                .findFirst();

        if (existente.isEmpty()) {
            throw new RuntimeException("No se encontró usuario para eliminar: " + dto.idUsuario);
        }

        usuarios.remove(existente.get());
        guardarUsuarios(usuarios);
        return existente.get();
    }

    @Override
    public Usuario login(String nombre, String apellido, String contrasenia) {
        return leerUsuarios().stream()
                .filter(u -> u.getNombre().equalsIgnoreCase(nombre)
                        && u.getApellido().equalsIgnoreCase(apellido)
                        && u.getContrasenia().equals(contrasenia))
                .findFirst()
                .orElse(null);
    }
}
