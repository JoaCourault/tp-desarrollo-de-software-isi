package com.isi.desa.Dao.Implementations;

import com.isi.desa.Dao.Interfaces.IEstadiaDAO;
import com.isi.desa.Dto.Estadia.EstadiaDTO;
import com.isi.desa.Model.Entities.Estadia.Estadia;
import com.isi.desa.Dao.Repositories.EstadiaRepository;
import com.isi.desa.Utils.Mappers.EstadiaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service("estadiaDAO")
public class EstadiaDAO implements IEstadiaDAO {

    @Autowired
    private EstadiaRepository estadiaRepository;

    @Override
    @Transactional
    public EstadiaDTO save(Estadia estadia) {
        return EstadiaMapper.entityToDto(estadiaRepository.save(estadia));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Estadia> findById(String id) {
        return estadiaRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Estadia> findAll() {
        return estadiaRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        estadiaRepository.deleteById(id);
    }
}
