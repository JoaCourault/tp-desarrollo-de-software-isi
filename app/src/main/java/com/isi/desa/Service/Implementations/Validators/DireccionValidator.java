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

    public DireccionValidator() {}

    @Override
    public Direccion create(DireccionDTO dto) {
        InvalidDirectionException validation = validate(dto);
        if (validation != null) throw validation;

        return new Direccion(
                dto.id,
                dto.calle,
                dto.numero,
                dto.departamento,
                dto.piso,
                dto.codigoPostal,
                dto.pais,
                dto.provincia,
                dto.localidad
        );
    }

    @Override
    public InvalidDirectionException validate(DireccionDTO dto) {

        if (dto == null) {
            return new InvalidDirectionException("La dirección no puede ser nula");
        }

        List<String> errores = new ArrayList<>();

        String error;

        if ((error = validatePais(dto.pais)) != null) errores.add(error);
        if ((error = validateProvincia(dto.provincia)) != null) errores.add(error);
        if ((error = validateLocalidad(dto.localidad)) != null) errores.add(error);
        if ((error = validateCodigoPostal(dto.codigoPostal)) != null) errores.add(error);
        if ((error = validateCalle(dto.calle)) != null) errores.add(error);
        if ((error = validateNumero(dto.numero)) != null) errores.add(error);

        if (!errores.isEmpty()) {
            return new InvalidDirectionException(
                    String.join(", ", errores)
            );
        }

        return null;
    }

    // ============================================================
    // VALIDACIONES INDIVIDUALES CORRECTAS
    // ============================================================

    private String validatePais(String pais) {
        return (pais == null || pais.trim().isEmpty())
                ? "El país es obligatorio"
                : null;
    }

    private String validateProvincia(String provincia) {
        return (provincia == null || provincia.trim().isEmpty())
                ? "La provincia es obligatoria"
                : null;
    }

    private String validateLocalidad(String localidad) {
        return (localidad == null || localidad.trim().isEmpty())
                ? "La localidad es obligatoria"
                : null;
    }

    private String validateCodigoPostal(String codigoPostal) {
        if (codigoPostal == null || codigoPostal.trim().isEmpty()) {
            return "El código postal es obligatorio";
        }

        try {
            int cp = Integer.parseInt(codigoPostal);
            if (cp <= 0) {
                return "El código postal debe ser un número positivo";
            }
        } catch (NumberFormatException e) {
            return "El código postal debe ser numérico";
        }

        return null;
    }

    private String validateCalle(String calle) {
        return (calle == null || calle.trim().isEmpty())
                ? "La calle es obligatoria"
                : null;
    }

    private String validateNumero(String numero) {
        if (numero == null || numero.trim().isEmpty()) {
            return "El número es obligatorio";
        }

        try {
            int n = Integer.parseInt(numero);
            if (n <= 0) {
                return "El número debe ser positivo";
            }
        } catch (NumberFormatException e) {
            return "El número debe ser numérico";
        }

        return null;
    }
}
