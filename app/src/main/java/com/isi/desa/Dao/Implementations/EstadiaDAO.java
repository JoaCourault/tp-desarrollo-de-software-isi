package com.isi.desa.Dao.Implementations;

import com.isi.desa.Dao.Interfaces.IEstadiaDAO;
import com.isi.desa.Dao.Repositories.EstadiaRepository;
import com.isi.desa.Dao.Repositories.HabitacionRepository;
import com.isi.desa.Dao.Repositories.HuespedRepository;
import com.isi.desa.Dao.Repositories.ReservaRepository;
import com.isi.desa.Dto.Estadia.EstadiaDTO;
import com.isi.desa.Model.Entities.Estadia.Estadia;
import com.isi.desa.Utils.Mappers.EstadiaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service("estadiaDAO")
public class EstadiaDAO implements IEstadiaDAO {

    @Autowired
    private EstadiaRepository repository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private HuespedRepository huespedRepository;

    @Autowired
    private HabitacionRepository habitacionRepository;
    // Métodos estándar

    @Override
    @Transactional
    public EstadiaDTO save(Estadia estadia) {
        Estadia saved = repository.save(estadia);
        return EstadiaMapper.entityToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Estadia> findById(String id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Estadia> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public Estadia modificar(EstadiaDTO dto) {
        Estadia entity = EstadiaMapper.dtoToEntity(dto);

        return repository.save(entity);
    }

    @Override
    @Transactional
    public Estadia eliminar(String id) {
        Optional<Estadia> entityOpt = repository.findById(id);
        entityOpt.ifPresent(repository::delete);
        return entityOpt.orElse(null);
    }
}