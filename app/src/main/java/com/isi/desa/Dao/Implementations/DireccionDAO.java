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
import java.util.UUID;

@Service
public class DireccionDAO implements IDireccionDAO {
    @Autowired
    private DireccionRepository repository;

    @Override
    @Transactional
    public Direccion crear(DireccionDTO direccion) {
        if (direccion.id == null || direccion.id.isBlank()) {
            direccion.id = java.util.UUID.randomUUID().toString();
        }
        if (repository.existsById(direccion.id)) {
            throw new RuntimeException("Ya existe una direccion con el ID: " + direccion.id);
        }
        Direccion nueva = DireccionMapper.dtoToEntity(direccion);
        return repository.save(nueva);
    }

    // --- EL MÉTODO QUE TE FALTABA ---
    @Transactional
    public void crear(DireccionDTO direccion, String nuevoIdHuesped) {
        if (direccion == null) return;

        // 1. Generar ID si no viene
        if (direccion.getId() == null || direccion.getId().isBlank()) {
            direccion.setId("DIR-" + UUID.randomUUID().toString().substring(0, 8));
        }

        // 2. Validar duplicados si es necesario
        if (repository.existsById(direccion.getId())) {
            throw new RuntimeException("Ya existe una direccion con el ID: " + direccion.getId());
        }

        // 3. Convertir a Entidad
        Direccion entidad = DireccionMapper.dtoToEntity(direccion);

        // 5. Guardar
        repository.save(entidad);
    }

    @Override
    @Transactional
    public Direccion modificar(DireccionDTO direccion) {
        if (!repository.existsById(direccion.id)){
            throw new RuntimeException("No se encontro la direccion con ID: " + direccion.id);
        }
        Direccion actualizada = DireccionMapper.dtoToEntity(direccion);
        return repository.save(actualizada);
    }

    @Override
    @Transactional
    public Direccion eliminar(DireccionDTO direccion) {
        Direccion existente = repository.findById(direccion.id)
                .orElseThrow(() -> new RuntimeException("No se encontro la direccion a eliminar: " + direccion.id));
        repository.delete(existente);
        return existente;
    }

    @Override
    @Transactional(readOnly = true)
    public Direccion obtener(DireccionDTO direccion) {
        return repository.findById(direccion.id)
                .orElseThrow(() -> new RuntimeException("No se encontro direccion con ID: " + direccion.id));
    }

    @Override
    @Transactional(readOnly = true)
    public Direccion getById(String id) {
        if (id == null) return null;
        // Retorna la dirección si existe, o null si no existe (sin lanzar error para que el mapper lo controle)
        return repository.findById(id).orElse(null);
    }
}