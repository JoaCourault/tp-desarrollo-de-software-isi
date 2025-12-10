package com.isi.desa.Dao.Implementations;

import com.isi.desa.Dao.Interfaces.IHabitacionDAO;
import com.isi.desa.Dao.Repositories.HabitacionRepository;
import com.isi.desa.Dto.Habitacion.HabitacionDTO;
import com.isi.desa.Model.Entities.Habitacion.HabitacionEntity;
import com.isi.desa.Utils.Mappers.HabitacionMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("habitacionDAO")
public class HabitacionDAO implements IHabitacionDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private HabitacionRepository repo;

    @Override
    @Transactional
    public HabitacionEntity crear(HabitacionDTO dto) {
        // 1. Validar si ya existe un ID proporcionado manualmente
        if (dto.idHabitacion != null && !dto.idHabitacion.isBlank() && repo.existsById(dto.idHabitacion)) {
            throw new RuntimeException("Ya existe una habitación con el ID: " + dto.idHabitacion);
        }

        // 2. Generación de ID automático si viene vacío (Formato HAB-001)
        if (dto.idHabitacion == null || dto.idHabitacion.isBlank()) {
            long count = repo.count();
            dto.idHabitacion = String.format("hab-%03d", count + 1);
        }

        // 3. Mapeo: El mapper instancia la subclase correcta (Suite, Standard, etc.)
        // basándose en el campo 'tipoHabitacion' del DTO.
        HabitacionEntity entity = HabitacionMapper.dtoToEntity(dto);

        // 4. Guardado
        return repo.save(entity);
    }

    @Override
    @Transactional
    public HabitacionEntity modificar(HabitacionDTO dto) {
        // 1. Validar existencia
        if (dto.idHabitacion == null || !repo.existsById(dto.idHabitacion)) {
            throw new RuntimeException("No se encontró habitación con ID: " + dto.idHabitacion);
        }

        // 2. Mapeo a entidad (Sobreescribe los datos)
        HabitacionEntity entity = HabitacionMapper.dtoToEntity(dto);

        // 3. Guardado
        return repo.save(entity);
    }

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

        // Aquí podrías agregar validaciones extra, por ejemplo:
        // si la habitación tiene reservas futuras o estadías activas, impedir borrado.

        repo.deleteById(id);
    }
}