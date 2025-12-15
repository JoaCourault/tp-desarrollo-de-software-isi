package com.isi.desa.Service.Implementations.Validators;

import com.isi.desa.Dto.Direccion.DireccionDTO;
import com.isi.desa.Exceptions.Direccion.InvalidDirectionException;
import com.isi.desa.Model.Entities.Direccion.Direccion;
import com.isi.desa.Service.Interfaces.Validators.IDireccionValidator;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Service
public class DireccionValidator implements IDireccionValidator {
    // Instancia unica (eager singleton)
    private static final DireccionValidator INSTANCE = new DireccionValidator();

    // Constructor privado
    private DireccionValidator() {}

    // Metodo publico para obtener la instancia
    public static DireccionValidator getInstance() {
        return INSTANCE;
    }

    public Direccion create(DireccionDTO direccionDTO) {
        InvalidDirectionException validationError = validate(direccionDTO);
        if (validationError != null) throw validationError;

        return new Direccion(
                direccionDTO.id,
                direccionDTO.calle,
                direccionDTO.numero,
                direccionDTO.departamento,
                direccionDTO.piso,
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

        List<String> errores = new ArrayList<>(); // Lista de Strings es más fácil
        String error;

        if ((error = validatePais(direccionDTO.pais)) != null) errores.add(error);
        if ((error = validateProvincia(direccionDTO.provincia)) != null) errores.add(error);
        if ((error = validateLocalidad(direccionDTO.localidad)) != null) errores.add(error);
        if ((error = validateCodigoPostal(direccionDTO.codigoPostal)) != null) errores.add(error);
        if ((error = validateCalle(direccionDTO.calle)) != null) errores.add(error);
        if ((error = validateNumero(direccionDTO.numero)) != null) errores.add(error);

        if (!errores.isEmpty()) {
            // Unimos los errores en un solo mensaje
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
        // Validación extra: Verificar que sea numérico y positivo (Regex)
        if (!codigoPostal.matches("\\d+")) {
            return "El codigo postal debe contener solo números positivos";
        }
        return null;
    }


    private String validateNumero(String numero) {

        if (numero == null || numero.trim().isEmpty()) {
            return "El numero es obligatorio";
        }
        // Validación extra: Verificar que sea numérico
        if (!numero.matches("\\d+")) {
            return "El numero de calle debe ser positivo";
        }
        return null;
    }
}