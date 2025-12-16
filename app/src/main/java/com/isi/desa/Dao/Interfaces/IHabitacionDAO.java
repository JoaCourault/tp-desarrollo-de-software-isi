package com.isi.desa.Dao.Interfaces;

import com.isi.desa.Dto.Habitacion.HabitacionDTO;
import com.isi.desa.Model.Entities.Habitacion.Habitacion;

import java.util.List;

public interface IHabitacionDAO {

    Habitacion modificar(HabitacionDTO dto);

        Habitacion save(Habitacion entity);

    Habitacion obtener(String id);

    List<Habitacion> listar();

    void eliminar(String id);

    boolean existsById(String id);

    long count();
}
