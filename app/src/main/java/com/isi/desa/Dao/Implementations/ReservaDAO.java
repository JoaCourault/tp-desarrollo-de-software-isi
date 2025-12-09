package com.isi.desa.Dao.Implementations;

import com.isi.desa.Dao.Interfaces.IReservaDAO;
import com.isi.desa.Dao.Repositories.ReservaRepository;
import com.isi.desa.Model.Entities.Reserva.Reserva;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service("reservaDAO")
public class ReservaDAO implements IReservaDAO {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ReservaRepository repository;

    @Transactional(readOnly = true)
    public Reserva getById(String id) {
        return repository.findById(id).orElse(null);
    }

    @Transactional
    public Reserva save(Reserva r) {
        return repository.save(r);
    }

    @Transactional(readOnly = true)
    public List<Reserva> buscarReservasSolapadas(String idHabitacion, LocalDate desde, LocalDate hasta) {
        return repository.findReservasEnRango(idHabitacion, desde, hasta);
    }

    @Transactional
    public Reserva update(Reserva r) {
        return repository.save(r);
    }
}
