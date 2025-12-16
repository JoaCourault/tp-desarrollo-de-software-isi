package com.isi.desa.Dao.Implementations;

import com.isi.desa.Dao.Interfaces.IHabitacionDAO;
import com.isi.desa.Dao.Repositories.HabitacionRepository;
import com.isi.desa.Dto.Habitacion.HabitacionDTO; // <--- IMPORTANTE
import com.isi.desa.Model.Entities.Habitacion.HabitacionEntity;
import com.isi.desa.Utils.Mappers.HabitacionMapper; // <--- IMPORTANTE
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("habitacionDAO")
public class HabitacionDAO implements IHabitacionDAO {

    @Autowired
    private HabitacionRepository repo;

    @Override
    @Transactional
    public HabitacionEntity save(HabitacionEntity entity) {
        return repo.save(entity);
    }


    @Override
    @Transactional
    public HabitacionEntity modificar(HabitacionDTO dto) {
        // Verificamos que exista antes de intentar modificar
        if (dto.idHabitacion == null || !repo.existsById(dto.idHabitacion)) {
            throw new RuntimeException("No se puede modificar. No se encontró habitación con ID: " + dto.idHabitacion);
        }

        // Convertimos el DTO a Entidad para que JPA lo guarde
        HabitacionEntity entity = HabitacionMapper.dtoToEntity(dto);

        // Al tener ID existente, .save() funciona como Update
        return repo.save(entity);
    }
    // -------------------------------------

    @Override
    @Transactional(readOnly = true)
    public HabitacionEntity obtener(String id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró habitación con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<HabitacionEntity> listar() {
        return repo.findAll();
    }

    @Override
    @Transactional
    public void eliminar(String id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("No se encontró habitación con ID: " + id);
        }
        repo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(String id) {
        return repo.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return repo.count();
    }
}