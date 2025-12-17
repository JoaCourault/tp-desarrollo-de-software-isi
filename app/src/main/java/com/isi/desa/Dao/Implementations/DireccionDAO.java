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

    @Autowired
    private DireccionMapper direccionMapper;

    @Override
    @Transactional
    public Direccion crear(DireccionDTO direccionDto) {
        // Validamos si viene con un ID pre-cargado que ya exista (para evitar sobrescrituras accidentales)
        if (direccionDto.id != null && !direccionDto.id.isBlank()) {
            if (repository.existsById(direccionDto.id)) {
                throw new RuntimeException("Ya existe una dirección con el ID: " + direccionDto.id);
            }
        }

        // Convertimos a Entidad. Si direccionDto.id es null, la entidad tendrá id null.
        Direccion nueva = direccionMapper.dtoToEntity(direccionDto);

        return repository.save(nueva);
    }

    @Transactional
    public void crear(DireccionDTO direccionDto, String nuevoIdHuesped) {
        if (direccionDto == null) return;

        if (direccionDto.id != null && !direccionDto.id.isBlank()) {
            if (repository.existsById(direccionDto.id)) {
                throw new RuntimeException("Ya existe una dirección con el ID: " + direccionDto.id);
            }
        }

        Direccion entidad = direccionMapper.dtoToEntity(direccionDto);
        repository.save(entidad);
    }

    @Override
    @Transactional
    public Direccion modificar(DireccionDTO direccion) {
        if (direccion.id == null || !repository.existsById(direccion.id)){
            throw new RuntimeException("No se encontró la dirección con ID: " + direccion.id);
        }

        // aca SÍ esperamos que el DTO traiga el ID para actualizar el registro correcto
        Direccion actualizada = direccionMapper.dtoToEntity(direccion);
        return repository.save(actualizada);
    }

    @Override
    @Transactional
    public Direccion eliminar(DireccionDTO direccion) {
        Direccion existente = repository.findById(direccion.id)
                .orElseThrow(() -> new RuntimeException("No se encontró la dirección a eliminar: " + direccion.id));
        repository.delete(existente);
        return existente;
    }

    @Override
    @Transactional(readOnly = true)
    public Direccion obtener(DireccionDTO direccion) {
        return repository.findById(direccion.id)
                .orElseThrow(() -> new RuntimeException("No se encontró dirección con ID: " + direccion.id));
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<Direccion> obtenerTodas() {
        return repository.findAll();
    }
    @Override
    @Transactional(readOnly = true)
    public Direccion getById(String id) {
        if (id == null) return null;
        return repository.findById(id).orElse(null);
    }
}
