package com.isi.desa.Dao.Implementations;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isi.desa.Dao.Interfaces.IUsuarioDAO;
import com.isi.desa.Dto.Usuario.UsuarioDTO;
import com.isi.desa.Model.Entities.Usuario.Usuario;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioDAO implements IUsuarioDAO {

    private static final String RES_DIR   = "jsonDataBase";
    private static final String JSON_FILE = "usuario.json";

    private final ObjectMapper mapper = new ObjectMapper();

    // === Helpers de ruta (solo build) ===
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

    // === Implementacion IUsuarioDAO ===
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
