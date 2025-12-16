package com.isi.desa.Service.Implementations.Validators;

import com.isi.desa.Dto.Habitacion.HabitacionDTO;
import com.isi.desa.Service.Interfaces.Validators.IHabitacionValidator;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class HabitacionValidator implements IHabitacionValidator {

    @Override
    public RuntimeException validateCreate(HabitacionDTO dto) {
        if (dto.numero == null || dto.numero <= 0)
            return new RuntimeException("El número debe ser positivo.");

        if (dto.piso == null)
            return new RuntimeException("El piso es obligatorio.");

        if (dto.tipoHabitacion == null || dto.tipoHabitacion.trim().isEmpty())
            return new RuntimeException("Debe especificar el tipo de habitación.");

        // Validar BigDecimal
        if (dto.precio == null || dto.precio.compareTo(BigDecimal.ZERO) < 0)
            return new RuntimeException("El precio es obligatorio y no puede ser negativo.");

        if (dto.capacidad == null || dto.capacidad <= 0)
            return new RuntimeException("La capacidad debe ser mayor a 0.");

        return null;
    }

    @Override
    public RuntimeException validateUpdate(HabitacionDTO dto) {
        if (dto.idHabitacion == null || dto.idHabitacion.trim().isEmpty())
            return new RuntimeException("ID obligatorio.");

        if (dto.precio != null && dto.precio.compareTo(BigDecimal.ZERO) < 0)
            return new RuntimeException("El precio no puede ser negativo.");

        return null;
    }
}