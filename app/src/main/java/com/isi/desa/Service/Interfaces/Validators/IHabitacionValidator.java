package com.isi.desa.Service.Interfaces.Validators;

import com.isi.desa.Dto.Habitacion.HabitacionDTO;

public interface IHabitacionValidator {
    RuntimeException validateCreate(HabitacionDTO dto);
    RuntimeException validateUpdate(HabitacionDTO dto);
}
