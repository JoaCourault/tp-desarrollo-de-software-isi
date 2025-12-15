package com.isi.desa.Dao.Implementations;

import com.isi.desa.Dao.Interfaces.IReservaDAO;
import com.isi.desa.Dao.Repositories.ReservaRepository;
import com.isi.desa.Model.Entities.Reserva.Reserva;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservaDAO implements IReservaDAO {

    @Autowired
    private ReservaRepository repository;

    @Override
    @Transactional
    public Reserva guardar(Reserva reserva) {
        return repository.save(reserva);
    }

    @Override
    public Reserva getById(String id) {
        return repository.findById(id).orElse(null);
    }
}