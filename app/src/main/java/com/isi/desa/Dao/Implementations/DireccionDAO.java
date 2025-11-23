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

@Service("direccionDAO")
public class DireccionDAO implements IDireccionDAO {

    @Autowired
    private DireccionRepository repository;

    // ============================================================
    // ===============           CREAR           ===================
    // ============================================================

    @Override
    @Transactional
    public Direccion crear(DireccionDTO dto) {

        // El ID SIEMPRE se genera aquí, no aceptamos uno del front
        dto.id = java.util.UUID.randomUUID().toString();

        Direccion nueva = DireccionMapper.dtoToEntity(dto);
        return repository.save(nueva);
    }

    // ============================================================
    // ===============         MODIFICAR         ===================
    // ============================================================

    @Override
    @Transactional
    public Direccion modificar(DireccionDTO dto) {
        if (dto.id == null || dto.id.isBlank()) {
            throw new RuntimeException("El ID de la dirección es obligatorio para modificar.");
        }

        if (!repository.existsById(dto.id)) {
            throw new RuntimeException("No se encontró la dirección con ID: " + dto.id);
        }

        Direccion actualizada = DireccionMapper.dtoToEntity(dto);
        return repository.save(actualizada);
    }

    // ============================================================
    // ===============          ELIMINAR         ===================
    // ============================================================

    @Override
    @Transactional
    public Direccion eliminar(DireccionDTO dto) {
        if (dto.id == null || dto.id.isBlank()) {
            throw new RuntimeException("El ID de la dirección es obligatorio.");
        }

        Direccion existente = repository.findById(dto.id)
                .orElseThrow(() -> new RuntimeException("No se encontró la dirección con ID: " + dto.id));

        repository.delete(existente);
        return existente;
    }

    // ============================================================
    // ===============           OBTENER         ===================
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public Direccion obtener(DireccionDTO dto) {
        if (dto.id == null || dto.id.isBlank()) {
            throw new RuntimeException("El ID de la dirección es obligatorio.");
        }

        return repository.findById(dto.id)
                .orElseThrow(() -> new RuntimeException("No se encontró dirección con ID: " + dto.id));
    }

    // ============================================================
    // ===============  OBTENER POR ID HUESPED  ===================
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public Direccion obtenerDireccionDeHuespedPorId(String idHuesped) {

        if (idHuesped == null || idHuesped.isBlank()) {
            throw new RuntimeException("El ID del huésped no puede ser nulo");
        }

        return repository.findByIdHuesped(idHuesped)
                .orElseThrow(() -> new RuntimeException(
                        "No se encontró dirección para el huésped con ID: " + idHuesped
                ));
    }
}
