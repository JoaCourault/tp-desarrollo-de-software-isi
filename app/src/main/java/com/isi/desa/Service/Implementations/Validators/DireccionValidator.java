package com.isi.desa.Service.Implementations.Validators;

import com.isi.desa.Dto.Direccion.DireccionDTO;
import com.isi.desa.Exceptions.Direccion.InvalidDirectionException;
import com.isi.desa.Service.Interfaces.Validators.IDireccionValidator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DireccionValidator implements IDireccionValidator {

    @Override
    public RuntimeException validate(DireccionDTO dto) {
        if (dto == null) {
            return new InvalidDirectionException("La dirección no puede ser nula.");
        }

        List<String> errores = new ArrayList<>();

        if (isNullOrEmpty(dto.calle)) errores.add("La calle es obligatoria.");
        if (isNullOrEmpty(dto.numero)) errores.add("El número es obligatorio.");
        if (isNullOrEmpty(dto.codigoPostal)) errores.add("El código postal es obligatorio.");
        if (isNullOrEmpty(dto.localidad)) errores.add("La localidad es obligatoria.");
        if (isNullOrEmpty(dto.provincia)) errores.add("La provincia es obligatoria.");
        if (isNullOrEmpty(dto.pais)) errores.add("El país es obligatorio.");

        if (!errores.isEmpty()) {
            String mensaje = errores.stream().collect(Collectors.joining(", "));
            return new InvalidDirectionException(mensaje);
        }

        return null; // Todo OK
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    @Override
    public RuntimeException validateUpdate(DireccionDTO dto) {
        // 1. Validaciones básicas (campos vacíos)
        RuntimeException errorBasico = this.validate(dto);
        if (errorBasico != null) return errorBasico;

        // 2. Validación específica de Update: Debe tener ID
        if (dto.id == null || dto.id.trim().isEmpty()) {
            return new InvalidDirectionException("Para modificar la dirección, el ID es obligatorio.");
        }

        return null;
    }
}