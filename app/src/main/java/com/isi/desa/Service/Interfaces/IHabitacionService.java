package com.isi.desa.Service.Interfaces;

import com.isi.desa.Dto.Habitacion.HabitacionDTO;
import com.isi.desa.Dto.Habitacion.HabitacionDisponibilidadDTO;

import java.time.LocalDate;
import java.util.List;

public interface IHabitacionService {
    HabitacionDTO crear(HabitacionDTO dto);
    HabitacionDTO modificar(HabitacionDTO dto);
    List<HabitacionDTO> listar();
}
