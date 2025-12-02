package com.isi.desa.Service.Implementations.Validators;

import com.isi.desa.Dto.Estadia.EstadiaDTO;
import com.isi.desa.Service.Interfaces.Validators.IEstadiaValidator;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class EstadiaValidator implements IEstadiaValidator {

    @Override
    public RuntimeException validateCreate(EstadiaDTO dto) {
        // Validar campos obligatorios básicos
        if (dto.checkIn == null) {
            return new RuntimeException("La fecha de Check-In es obligatoria.");
        }

        if (dto.cantNoches == null || dto.cantNoches <= 0) {
            return new RuntimeException("La cantidad de noches debe ser mayor a 0.");
        }

        // Validar coherencia de fechas (Si checkOut viene informado)
        if (dto.checkOut != null && dto.checkIn.isAfter(dto.checkOut)) {
            return new RuntimeException("La fecha de Check-Out no puede ser anterior al Check-In.");
        }

        // Validar montos (Si se informan al crear)
        if (dto.valorTotalEstadia != null && dto.valorTotalEstadia.compareTo(BigDecimal.ZERO) < 0) {
            return new RuntimeException("El valor total de la estadía no puede ser negativo.");
        }

        // Validar relaciones obligatorias según tu Entity (nullable = false)
        // Nota: Depende de tu lógica de negocio si la factura se crea antes o después.
        // Si es obligatoria al momento de crear la estadía:
        if (dto.idFactura == null || dto.idFactura.trim().isEmpty()) {
            return new RuntimeException("El ID de la factura es obligatorio.");
        }

        if (dto.idReserva == null || dto.idReserva.trim().isEmpty()) {
            return new RuntimeException("El ID de la reserva es obligatorio.");
        }

        return null;
    }

    @Override
    public RuntimeException validateUpdate(EstadiaDTO dto) {
        // Para actualizar, el ID es fundamental
        if (dto.idEstadia == null || dto.idEstadia.trim().isEmpty()) {
            return new RuntimeException("Debe indicar el ID de la estadía para modificarla.");
        }

        // Validar coherencia si se intentan cambiar las fechas en el update
        //if (dto.checkIn != null && dto.checkOut != null && dto.checkIn.isAfter(dto.checkOut)) {
        //    return new RuntimeException("La fecha de Check-Out no puede ser anterior al Check-In.");
        //}

        return null;
    }
}