package com.isi.desa.Dao.Implementations;

import com.isi.desa.Dao.Interfaces.IDireccionDAO;
import com.isi.desa.Dao.Repositories.DireccionRepository;
import com.isi.desa.Dto.Direccion.DireccionDTO;
import com.isi.desa.Model.Entities.Direccion.Direccion;
import com.isi.desa.Utils.Mappers.DireccionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class DireccionDAO implements IDireccionDAO {

    @Autowired
    private DireccionRepository repository;

    @Override
    @Transactional
    public Direccion crear(DireccionDTO direccion) {
        if (direccion.idDireccion == null || direccion.idDireccion.isBlank()) {
            direccion.idDireccion = java.util.UUID.randomUUID().toString();
        }
        if (repository.existsById(direccion.idDireccion)) {
            throw new RuntimeException("Ya existe una direccion con el ID: " + direccion.idDireccion);
        }
        Direccion nueva = DireccionMapper.dtoToEntity(direccion);
        return repository.save(nueva);
    }

    @Override
    @Transactional
    public Direccion modificar(DireccionDTO direccion) {
        if (!repository.existsById(direccion.idDireccion)) {
            throw new RuntimeException("No se encontro la direccion con ID: " + direccion.idDireccion);
        }
        Direccion actualizada = DireccionMapper.dtoToEntity(direccion);
        return repository.save(actualizada);
    }

    @Override
    @Transactional
    public Direccion eliminar(DireccionDTO direccion) {
        Direccion existente = repository.findById(direccion.idDireccion)
                .orElseThrow(() -> new RuntimeException("No se encontro la direccion a eliminar: " + direccion.idDireccion));
        repository.delete(existente);
        return existente;
    }

    @Override
    @Transactional(readOnly = true)
    public Direccion obtener(DireccionDTO direccion) {
        return repository.findById(direccion.idDireccion)
                .orElseThrow(() -> new RuntimeException("No se encontro direccion con ID: " + direccion.idDireccion));
    }

    // --- ESTE ES EL MÉTODO QUE TE FALTABA ---
    // Usamos el mismo repository que en los otros métodos.
    @Override
    @Transactional(readOnly = true)
    public Direccion getById(String id) {
        if (id == null) return null;
        // Retorna la dirección si existe, o null si no existe (sin lanzar error para que el mapper lo controle)
        return repository.findById(id).orElse(null);
    }
}