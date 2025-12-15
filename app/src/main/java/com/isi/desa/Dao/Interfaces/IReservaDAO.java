package com.isi.desa.Dao.Interfaces;

import com.isi.desa.Model.Entities.Reserva.Reserva;

public interface IReservaDAO {
    Reserva guardar(Reserva reserva);
    Reserva getById(String id);
}