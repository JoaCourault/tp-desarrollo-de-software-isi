package com.isi.desa.Service.Implementations.Validators;

import com.isi.desa.Dto.Habitacion.HabitacionDTO;
import com.isi.desa.Service.Interfaces.Validators.IHabitacionValidator;
import org.springframework.stereotype.Component;

@Component
public class HabitacionValidator implements IHabitacionValidator {

    @Override
    public RuntimeException validateCreate(HabitacionDTO dto) {
        // 1. Campos obligatorios básicos
        if (dto.numero == null || dto.numero <= 0) {
            return new RuntimeException("El número de habitación es obligatorio y debe ser positivo.");
        }

        if (dto.piso == null) {
            return new RuntimeException("El piso es obligatorio.");
        }

        // 2. Tipo de Habitación (CRUCIAL para la herencia)
        if (dto.tipoHabitacion == null || dto.tipoHabitacion.trim().isEmpty()) {
            return new RuntimeException("Debe especificar el tipo de habitación (Ej: 'Suite Doble', 'Individual Estándar').");
        }

        // 3. Validar Precio (Float)
        if (dto.precio == null || dto.precio < 0) {
            return new RuntimeException("El precio es obligatorio y no puede ser negativo.");
        }

        // 4. Validar Capacidad
        if (dto.capacidad == null || dto.capacidad <= 0) {
            return new RuntimeException("La capacidad debe ser mayor a 0.");
        }

        // 5. Validaciones específicas según el tipo (Opcional pero recomendado)
        // Ejemplo: Si es "Individual Estándar", debe tener al menos 1 cama individual definida.
        if ("Individual Estándar".equalsIgnoreCase(dto.tipoHabitacion) &&
                (dto.cantidadCamasIndividual == null || dto.cantidadCamasIndividual <= 0)) {
            return new RuntimeException("Para el tipo 'Individual Estándar' debe indicar la cantidad de camas individuales.");
        }

        return null;
    }

    @Override
    public RuntimeException validateUpdate(HabitacionDTO dto) {
        // 1. ID Obligatorio
        if (dto.idHabitacion == null || dto.idHabitacion.trim().isEmpty()) {
            return new RuntimeException("Debe indicar el ID de la habitación para modificarla.");
        }

        // 2. Si se actualiza el precio, validar que no sea negativo
        if (dto.precio != null && dto.precio < 0) {
            return new RuntimeException("El precio no puede ser negativo.");
        }

        return null;
    }
}