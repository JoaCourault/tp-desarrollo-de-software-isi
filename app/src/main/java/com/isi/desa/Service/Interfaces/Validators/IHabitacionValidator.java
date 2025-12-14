package com.isi.desa.Service.Interfaces.Validators;

import com.isi.desa.Model.Entities.Habitacion.Habitacion;

public interface IHabitacionValidator {
    Boolean validateExistById(String idHabitacion);
    Boolean validateHabitacionDisponibleById(String idHabitacion);
}
