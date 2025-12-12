package com.isi.desa.Dao.Interfaces;

import com.isi.desa.Model.Entities.Reserva.Reserva;
import java.util.List;

public interface IReservaDAO {
    // Método solicitado por el Diagrama de Secuencia (guardar)
    Reserva guardar(Reserva reserva);

    Reserva getById(String id);
    // Metodos para borrar reservas
    List<Reserva> buscarPorHuesped(String apellido, String nombre);
    void eliminar(String idReserva);
}