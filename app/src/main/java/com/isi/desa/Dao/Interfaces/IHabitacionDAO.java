package com.isi.desa.Dao.Interfaces;

import com.isi.desa.Dto.Habitacion.HabitacionDTO;
import com.isi.desa.Model.Entities.Habitacion.HabitacionEntity;

import java.util.List;

public interface IHabitacionDAO {

    HabitacionEntity modificar(HabitacionDTO dto);

        HabitacionEntity save(HabitacionEntity entity);

    HabitacionEntity obtener(String id);

    List<HabitacionEntity> listar();

    void eliminar(String id);

    boolean existsById(String id);

    long count();
}
