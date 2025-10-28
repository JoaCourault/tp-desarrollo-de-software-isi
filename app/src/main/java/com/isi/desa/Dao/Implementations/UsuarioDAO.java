package com.isi.desa.Dao.Implementations;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isi.desa.Dao.Interfaces.IUsuarioDAO;
import com.isi.desa.Dto.Usuario.UsuarioDTO;
import com.isi.desa.Model.Entities.Usuario.Usuario;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioDAO implements IUsuarioDAO {

    private static final String RES_DIR   = "jsonDataBase";
    private static final String JSON_FILE = "usuario.json";

    private final ObjectMapper mapper = new ObjectMapper();

    // ===== Helpers de ruta =====
    private File getJsonFileForRead() {
        try {
            String resourcePath = RES_DIR + "/" + JSON_FILE;
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            URL url = cl.getResource(resourcePath);
            if (url != null && !"jar".equalsIgnoreCase(url.getProtocol())) {
                File f = Paths.get(url.toURI()).toFile();
                if (f.exists()) return f;
            }
            return getJsonFileForWrite();
        } catch (Exception e) {
            return getJsonFileForWrite();
        }
    }

    private File getJsonFileForWrite() {
        File dev = Paths.get("src","main","resources",RES_DIR,JSON_FILE).toFile();
        try {
            ensureFile(dev);
            return dev;
        } catch (Exception ignore) {
            File external = Paths.get("data",RES_DIR,JSON_FILE).toFile();
            try {
                ensureFile(external);
                return external;
            } catch (Exception ex) {
                throw new RuntimeException("No se pudo crear archivo JSON: " + external.getAbsolutePath(), ex);
            }
        }
    }

    private void ensureFile(File f) throws Exception {
        File p = f.getParentFile();
        if (p != null && !p.exists()) p.mkdirs();
        if (!f.exists()) f.createNewFile();
    }

    // ===== IO =====
    private List<Usuario> leerUsuarios() {
        File file = getJsonFileForRead();
        try {
            if (file.length() == 0) return new ArrayList<>();
            return mapper.readValue(file, new TypeReference<List<Usuario>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Error al leer " + JSON_FILE, e);
        }
    }

    private void guardarUsuarios(List<Usuario> usuarios) {
        try {
            File file = getJsonFileForWrite();
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, usuarios);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar " + JSON_FILE, e);
        }
    }

    private Usuario dtoToEntity(UsuarioDTO dto) {
        return new Usuario(dto.idUsuario, dto.contrasenia, dto.nombre, dto.apellido);
    }

    // ===== Implementación IUsuarioDAO =====
    @Override
    public Usuario crear(UsuarioDTO dto) {
        List<Usuario> usuarios = leerUsuarios();

        boolean existe = usuarios.stream().anyMatch(u -> u.getIdUsuario().equals(dto.idUsuario));
        if (existe) throw new RuntimeException("Ya existe un usuario con ID " + dto.idUsuario);

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

        if (existente.isEmpty())
            throw new RuntimeException("No se encontro usuario con ID: " + dto.idUsuario);

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
                .orElseThrow(() -> new RuntimeException("No se encontro usuario con ID: " + idUsuario));
    }

    @Override
    public Usuario eliminar(UsuarioDTO dto) {
        List<Usuario> usuarios = leerUsuarios();

        Optional<Usuario> existente = usuarios.stream()
                .filter(u -> u.getIdUsuario().equals(dto.idUsuario))
                .findFirst();

        if (existente.isEmpty())
            throw new RuntimeException("No se encontro usuario para eliminar: " + dto.idUsuario);

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
