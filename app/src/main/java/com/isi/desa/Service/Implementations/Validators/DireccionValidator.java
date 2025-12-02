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

    // CORRECCIÓN: Eliminamos el Singleton manual (INSTANCE, constructor privado, getInstance)
    // Spring manejará la instancia con @Autowired donde se necesite.

    public DireccionValidator() {
        // Constructor público vacío para Spring
    }

    // pero lo ideal es usar inyección de dependencias.
    // Si algún otro validator usa 'DireccionValidator.getInstance()', cámbialo a 'new DireccionValidator()'
    // o inyéctalo con @Autowired.
    public static DireccionValidator getInstance() {
        return new DireccionValidator();
    }

    @Override
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
        // --- AGREGA ESTAS LINEAS PARA VER EN CONSOLA ---
        System.out.println("--- VALIDANDO DIRECCION ---");
        System.out.println("Calle recibida: '" + direccionDTO.calle + "'");
        System.out.println("Numero recibido: '" + direccionDTO.numero + "'");
        System.out.println("CP recibido: '" + direccionDTO.codigoPostal + "'");
        System.out.println("---------------------------");
        // -----------------------------------------------
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
        if ((error = validateCodigoPostal(direccionDTO.codigoPostal)) != null) {
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
        // CORREGIDO: Antes tenías !codigoPostal.isEmpty() (Si NO está vacío = error).
        // AHORA: Si es null O está vacío = error.
        return (codigoPostal == null || codigoPostal.trim().isEmpty()) ? "El codigo postal es obligatorio y debe ser positivo" : null;
    }

    private String validateCalle(String calle) {
        return (calle == null || calle.trim().isEmpty()) ? "La calle es obligatoria" : null;
    }

    private String validateNumero(String numero) {
        // CORREGIDO: Igual que el CP, eliminamos el '!' y agregamos trim().
        return (numero == null || numero.trim().isEmpty()) ? "El numero es obligatorio y debe ser positivo" : null;
    }
}