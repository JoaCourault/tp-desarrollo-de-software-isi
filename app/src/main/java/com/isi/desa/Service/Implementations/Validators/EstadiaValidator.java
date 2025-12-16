package com.isi.desa.Service.Implementations.Validators;

import com.isi.desa.Dto.Estadia.EstadiaDTO;
import com.isi.desa.Service.Interfaces.Validators.IEstadiaValidator;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class EstadiaValidator implements IEstadiaValidator {

    @Override
    public RuntimeException validateCreate(EstadiaDTO dto) {
        // 1. Validar Fechas
        if (dto.checkIn == null) {
            return new RuntimeException("La fecha de Check-In es obligatoria.");
        }

        // 2. Validar Noches
        if (dto.cantNoches == null || dto.cantNoches <= 0) {
            return new RuntimeException("La cantidad de noches debe ser mayor a 0.");
        }

        // 3. Validar Coherencia Fechas
        if (dto.checkOut != null && dto.checkIn.isAfter(dto.checkOut)) {
            return new RuntimeException("La fecha de Check-Out no puede ser anterior al Check-In.");
        }

        // 4. Validar Costo (BigDecimal comparison)
        if (dto.valorTotalEstadia != null && dto.valorTotalEstadia.compareTo(BigDecimal.ZERO) < 0) {
            return new RuntimeException("El valor total de la estadía no puede ser negativo.");
        }

        return null;
    }

    @Override
    public RuntimeException validateUpdate(EstadiaDTO dto) {
        if (dto.idEstadia == null || dto.idEstadia.trim().isEmpty()) {
            return new RuntimeException("Debe indicar el ID de la estadía para modificarla.");
        }
        return null;
    }
}