package com.isi.desa.Dao.Interfaces;

import com.isi.desa.Model.Entities.Reserva.Reserva;

public interface IReservaDAO {
    // Método solicitado por el Diagrama de Secuencia (guardar)
    Reserva guardar(Reserva reserva);

    // Puedes agregar otros métodos futuros aquí (buscar, eliminar, etc.)
    Reserva getById(String id);
}