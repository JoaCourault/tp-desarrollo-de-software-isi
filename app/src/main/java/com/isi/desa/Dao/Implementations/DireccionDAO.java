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
        if (direccion.id == null || direccion.id.isBlank()) {
            direccion.id = java.util.UUID.randomUUID().toString();
        }
        if (repository.existsById(direccion.id)) {
            throw new RuntimeException("Ya existe una direccion con el ID: " + direccion.id);
        }
        Direccion nueva = DireccionMapper.dtoToEntity(direccion);
        return repository.save(nueva);
    }

    @Override
    @Transactional
    public Direccion modificar(DireccionDTO direccion) {
        if (!repository.existsById(direccion.id)) {
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
}
