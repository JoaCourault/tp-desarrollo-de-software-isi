package com.isi.desa.Service.Implementations.Validators;

import com.isi.desa.Dto.Habitacion.HabitacionDTO;
import com.isi.desa.Service.Interfaces.Validators.IHabitacionValidator;
import org.springframework.stereotype.Component;

@Component
public class HabitacionValidator implements IHabitacionValidator {

    @Override
    public RuntimeException validateCreate(HabitacionDTO dto) {
        if (dto.id_habitacion == null)
            return new RuntimeException("El ID de habitación es obligatorio.");

        if (dto.numero == null)
            return new RuntimeException("El número de habitación es obligatorio.");

        return null;
    }

    @Override
    public RuntimeException validateUpdate(HabitacionDTO dto) {
        if (dto.id_habitacion == null)
            return new RuntimeException("Debe indicar el ID de habitación para modificar.");
        return null;
    }
}
