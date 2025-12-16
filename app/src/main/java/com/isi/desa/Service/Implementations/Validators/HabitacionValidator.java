package com.isi.desa.Service.Implementations.Validators;

import com.isi.desa.Dao.Repositories.HabitacionRepository;
import com.isi.desa.Model.Entities.Habitacion.Habitacion;
import com.isi.desa.Model.Enums.EstadoHabitacion;
import com.isi.desa.Service.Interfaces.Validators.IHabitacionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HabitacionValidator implements IHabitacionValidator {
    @Autowired
    HabitacionRepository habitacionRepository;

    @Override
    public Boolean validateExistById(String idHabitacion) {
        Habitacion habitacion = habitacionRepository.findById(String.valueOf(Long.valueOf(idHabitacion))).orElse(null);
        return habitacion != null;
    }

    @Override
    public Boolean validateHabitacionDisponibleById(String idHabitacion) {
        Habitacion habitacion = habitacionRepository.findById(String.valueOf(Long.valueOf(idHabitacion))).orElse(null);
        if (habitacion == null) {
            return false;
        }
        return habitacion.getEstado() == EstadoHabitacion.DISPONIBLE;
    }
}
