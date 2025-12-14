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
    // Instancia unica (eager singleton)
    private static final DireccionValidator INSTANCE = new DireccionValidator();

    // Constructor privado
    private DireccionValidator() {}

    // Metodo publico para obtener la instancia
    public static DireccionValidator getInstance() {
        return INSTANCE;
    }

    @Override
    public Direccion create(DireccionDTO direccionDTO) {
        InvalidDirectionException validationError = validate(direccionDTO);
        if (validationError != null) throw validationError;

        return new Direccion(
                direccionDTO.idDireccion,
                direccionDTO.calle,
                direccionDTO.numero,
                direccionDTO.departamento,
                direccionDTO.piso,
                direccionDTO.cp,
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

        List<RuntimeException> errores = new ArrayList<>();
        String error;
        if ((error = validatePais(direccionDTO.pais)) != null) {
            errores.add(new InvalidDirectionException(error));
        }
        if ((error = validateProvincia(direccionDTO.provincia)) != null) {
            errores.add(new InvalidDirectionException(error));
        }
        if ((error = validateLocalidad(direccionDTO.localidad)) != null) {
            errores.add(new InvalidDirectionException(error));
        }
        if ((error = validateCodigoPostal(direccionDTO.cp)) != null) {
            errores.add(new InvalidDirectionException(error));
        }
        if ((error = validateCalle(direccionDTO.calle)) != null) {
            errores.add(new InvalidDirectionException(error));
        }
        if ((error = validateNumero(direccionDTO.numero)) != null) {
            errores.add(new InvalidDirectionException(error));
        }

        if (!errores.isEmpty()) return new InvalidDirectionException(
                errores.stream()
                        .map(RuntimeException::getMessage)
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("")
        );

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

    private String validateCodigoPostal(String codigoPostal) {
        return (codigoPostal == null || !codigoPostal.isEmpty()) ? "El codigo postal es obligatorio y debe ser positivo" : null;
    }

    private String validateCalle(String calle) {
        return (calle == null || calle.trim().isEmpty()) ? "La calle es obligatoria" : null;
    }

    private String validateNumero(String numero) {
        return (numero == null || !numero.isEmpty()) ? "El numero es obligatorio y debe ser positivo" : null;
    }
}
