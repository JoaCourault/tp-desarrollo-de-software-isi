package com.isi.desa.Dao.Interfaces;

import com.isi.desa.Dto.Habitacion.HabitacionDTO;
import com.isi.desa.Model.Entities.Habitacion.HabitacionEntity;
import java.util.List;

public interface IHabitacionDAO {
    HabitacionEntity crear(HabitacionDTO dto);
    HabitacionEntity modificar(HabitacionDTO dto);
    HabitacionEntity obtener(String id);
    List<HabitacionEntity> listar();
    void eliminar(String id);
}