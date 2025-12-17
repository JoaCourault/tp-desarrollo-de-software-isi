package com.isi.desa.Service.Implementations.Validators;

import com.isi.desa.Dto.Direccion.DireccionDTO;
import com.isi.desa.Exceptions.Direccion.InvalidDirectionException;
import com.isi.desa.Model.Entities.Direccion.Direccion;
import com.isi.desa.Service.Interfaces.Validators.IDireccionValidator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DireccionValidator implements IDireccionValidator {


    public Direccion create(DireccionDTO direccionDTO) {
        InvalidDirectionException validationError = validate(direccionDTO);
        if (validationError != null) throw validationError;

        Integer pisoInt = null;
        if (direccionDTO.piso != null && !direccionDTO.piso.isBlank()) {
            try {
                pisoInt = Integer.parseInt(direccionDTO.piso);
            } catch (NumberFormatException e) {

                throw new InvalidDirectionException("El formato del piso es inválido (debe ser numérico).");
            }
        }

        return new Direccion(
                direccionDTO.id,
                direccionDTO.calle,
                direccionDTO.numero,
                direccionDTO.departamento,
                pisoInt,
                direccionDTO.codigoPostal,
                direccionDTO.pais,
                direccionDTO.provincia,
                direccionDTO.localidad
        );
    }

    @Override
    public InvalidDirectionException validate(DireccionDTO direccionDTO) {
        if (direccionDTO == null) {
            return new InvalidDirectionException("La direccion no puede ser nula");
        }

        List<String> errores = new ArrayList<>();
        String error;

        if ((error = validatePais(direccionDTO.pais)) != null) errores.add(error);
        if ((error = validateProvincia(direccionDTO.provincia)) != null) errores.add(error);
        if ((error = validateLocalidad(direccionDTO.localidad)) != null) errores.add(error);
        if ((error = validateCodigoPostal(direccionDTO.codigoPostal)) != null) errores.add(error);
        if ((error = validateCalle(direccionDTO.calle)) != null) errores.add(error);
        if ((error = validateNumero(direccionDTO.numero)) != null) errores.add(error);

        // Agregamos validación de piso
        if ((error = validatePiso(direccionDTO.piso)) != null) errores.add(error);

        if (!errores.isEmpty()) {
            String mensajeFinal = String.join(", ", errores);
            return new InvalidDirectionException(mensajeFinal);
        }

        return null;
    }

    private String validatePais(String pais) {
        return (pais == null || pais.trim().isEmpty()) ? "El pais es obligatorio" : null;
    }

    private String validateProvincia(String provincia) {
        return (provincia == null || provincia.trim().isEmpty()) ? "La provincia es obligatoria" : null;
    }

    private String validateLocalidad(String localidad) {
        return (localidad == null || localidad.trim().isEmpty()) ? "La localidad es obligatoria" : null;
    }

    private String validateCalle(String calle) {
        return (calle == null || calle.trim().isEmpty()) ? "La calle es obligatoria" : null;
    }

    private String validateCodigoPostal(String codigoPostal) {
        if (codigoPostal == null || codigoPostal.trim().isEmpty()) {
            return "El codigo postal es obligatorio";
        }
        if (!codigoPostal.matches("^[a-zA-Z0-9]+$")) {
            return "El codigo postal contiene caracteres inválidos (solo se permiten letras y números sin espacios)";
        }
        return null;
    }

    private String validateNumero(String numero) {
        if (numero == null || numero.trim().isEmpty()) {
            return "El numero es obligatorio";
        }

        return null;
    }

    // Validación extra para el piso
    private String validatePiso(String piso) {
        if (piso != null && !piso.isBlank()) {
            if (!piso.matches("\\d+")) {
                return "El piso debe ser un número entero";
            }
        }
        return null;
    }
}