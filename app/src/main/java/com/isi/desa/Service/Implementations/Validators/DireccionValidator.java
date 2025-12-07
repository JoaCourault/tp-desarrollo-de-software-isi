package com.isi.desa.Service.Implementations.Validators;

import com.isi.desa.Dto.Direccion.DireccionDTO;
import com.isi.desa.Exceptions.Direccion.InvalidDirectionException;
import com.isi.desa.Model.Entities.Direccion.Direccion;
import com.isi.desa.Service.Interfaces.Validators.IDireccionValidator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DireccionValidator implements IDireccionValidator {

    @Override
    public Direccion create(DireccionDTO dto) {
        InvalidDirectionException validationError = validate(dto);
        if (validationError != null) throw validationError;

        // Nota: Asegúrate de usar los Getters del DTO
        return new Direccion(
                dto.getId(),
                dto.getCalle(),
                dto.getNumero(),
                dto.getDepartamento(),
                // Conversión segura de Piso (String -> Integer) si lo necesitas como Int en la entidad
                (dto.getPiso() != null && dto.getPiso().matches("\\d+")) ? Integer.parseInt(dto.getPiso()) : null,
                dto.getCodigoPostal(),
                dto.getLocalidad(),
                dto.getProvincia(),
                dto.getPais()
        );
    }

    @Override
    public InvalidDirectionException validate(DireccionDTO dto) {
        if (dto == null) {
            return new InvalidDirectionException("La direccion no puede ser nula");
        }

        List<String> errores = new ArrayList<>();

        // Usamos los Getters del DTO
        String error;
        if ((error = validatePais(dto.getPais())) != null) errores.add(error);
        if ((error = validateProvincia(dto.getProvincia())) != null) errores.add(error);
        if ((error = validateLocalidad(dto.getLocalidad())) != null) errores.add(error);
        if ((error = validateCodigoPostal(dto.getCodigoPostal())) != null) errores.add(error);
        if ((error = validateCalle(dto.getCalle())) != null) errores.add(error);
        if ((error = validateNumero(dto.getNumero())) != null) errores.add(error);

        if (!errores.isEmpty()) {
            return new InvalidDirectionException(String.join(", ", errores));
        }

        return null;
    }

    // --- MÉTODOS DE VALIDACIÓN CORREGIDOS ---

    private String validatePais(String pais) {
        return isBlank(pais) ? "El país es obligatorio" : null;
    }

    private String validateProvincia(String provincia) {
        return isBlank(provincia) ? "La provincia es obligatoria" : null;
    }

    private String validateLocalidad(String localidad) {
        return isBlank(localidad) ? "La localidad es obligatoria" : null;
    }

    private String validateCodigoPostal(String codigoPostal) {
        if (isBlank(codigoPostal)) {
            return "El código postal es obligatorio";
        }
        // Validamos que sea numérico (regex para solo dígitos)
        if (!codigoPostal.matches("\\d+")) {
            return "El código postal debe contener solo números positivos";
        }
        return null;
    }

    private String validateCalle(String calle) {
        return isBlank(calle) ? "La calle es obligatoria" : null;
    }

    private String validateNumero(String numero) {
        if (isBlank(numero)) {
            return "El número es obligatorio";
        }
        // Validamos que sea numérico
        if (!numero.matches("\\d+")) {
            return "El número de calle debe ser un valor positivo";
        }
        return null;
    }

    // Helper para verificar nulos o vacíos
    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}