package com.isi.desa.Dao.Interfaces;

import com.isi.desa.Model.Entities.Reserva.Reserva;
import java.time.LocalDate;
import java.util.List;

public interface IReservaDAO {
    Reserva getById(String id);
    Reserva save(Reserva r);
    List<Reserva> buscarReservasSolapadas(String idHabitacion, LocalDate desde, LocalDate hasta);
    Reserva update(Reserva r);
}
