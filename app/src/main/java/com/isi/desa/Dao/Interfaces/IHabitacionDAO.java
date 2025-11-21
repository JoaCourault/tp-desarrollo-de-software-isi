package com.isi.desa.Dao.Interfaces;

import com.isi.desa.Model.Entities.Habitacion.HabitacionEntity;

import java.util.List;

public interface IHabitacionDAO {

    HabitacionEntity crear(HabitacionEntity h);
    HabitacionEntity modificar(HabitacionEntity h);
    HabitacionEntity obtener(String id);
    List<HabitacionEntity> listar();
}
