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
    public Direccion save(Direccion direccion) {
        return repository.save(direccion);
    }

    @Override
    @Transactional
    public Direccion modificar(DireccionDTO dto) {
        Direccion existente = repository.findById(dto.id)
                .orElseThrow(() -> new RuntimeException("No se encontró la dirección con ID: " + dto.id));

        // si querés mantener el mismo id, ok:
        Direccion actualizado = DireccionMapper.dtoToEntity(dto);
        actualizado.setIdDireccion(existente.getIdDireccion());

        return repository.save(actualizado);
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
    public Direccion obtenerDireccionDeHuespedPorId(String idHuesped) {
        String id = Optional.ofNullable(idHuesped)
                .orElseThrow(() -> new RuntimeException("El ID del huesped no puede ser nulo"));
        return repository.findByIdHuesped(id)
                .orElseThrow(() -> new RuntimeException("No se encontró dirección para el huesped con ID: " + id));
    }
}
