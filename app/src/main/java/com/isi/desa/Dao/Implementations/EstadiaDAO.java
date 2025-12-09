package com.isi.desa.Dao.Implementations;

import com.isi.desa.Dao.Interfaces.IEstadiaDAO;
import com.isi.desa.Dao.Repositories.EstadiaRepository;
import com.isi.desa.Dto.Estadia.EstadiaDTO;
import com.isi.desa.Model.Entities.Estadia.Estadia;
import com.isi.desa.Utils.Mappers.EstadiaMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service("estadiaDAO")
public class EstadiaDAO implements IEstadiaDAO {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private EstadiaRepository repository;

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
}
