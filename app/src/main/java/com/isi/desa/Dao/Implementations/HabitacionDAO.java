package com.isi.desa.Dao.Implementations;

import com.isi.desa.Dao.Interfaces.IHabitacionDAO;
import com.isi.desa.Dao.Repositories.HabitacionRepository;
import com.isi.desa.Model.Entities.Habitacion.HabitacionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HabitacionDAO implements IHabitacionDAO {

    @Autowired
    private HabitacionRepository repo;

    @Override
    public HabitacionEntity crear(HabitacionEntity h) {
        return repo.save(h);
    }

    @Override
    public HabitacionEntity modificar(HabitacionEntity h) {
        return repo.save(h);
    }

    @Override
    public HabitacionEntity obtener(String id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public List<HabitacionEntity> listar() {
        return repo.findAll();
    }
}
