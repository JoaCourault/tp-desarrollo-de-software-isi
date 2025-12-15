package com.isi.desa.Dao.Implementations;

import com.isi.desa.Dao.Interfaces.IReservaDAO;
import com.isi.desa.Dao.Repositories.ReservaRepository;
import com.isi.desa.Model.Entities.Reserva.Reserva;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

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

    // Implementaciones para borrar reservas
    @Override
    public List<Reserva> buscarPorHuesped(String apellido, String nombre) {
        // 1. Preparamos el Apellido
        String apellidoQuery = apellido + "%";
        // 2. Preparamos el Nombre
        String nombreQuery = null;
        if (nombre != null && !nombre.trim().isEmpty()) {
            nombreQuery = nombre + "%";
        }
        // 3. Llamamos al Repo con los strings ya preparados
        return repository.buscarPorHuesped(apellidoQuery, nombreQuery);
    }

    @Override
    @Transactional
    public void eliminar(String idReserva) {
        repository.deleteById(idReserva);
    }
}