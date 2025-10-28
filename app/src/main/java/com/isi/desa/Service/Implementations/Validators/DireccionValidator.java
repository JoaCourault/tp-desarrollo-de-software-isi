package com.isi.desa.Service.Implementations.Validators;

import com.isi.desa.Dto.Direccion.DireccionDTO;
import com.isi.desa.Exceptions.Direccion.InvalidDirectionException;
import com.isi.desa.Model.Entities.Direccion.Direccion;
import com.isi.desa.Service.Interfaces.Validators.IDireccionValidator;
import java.util.ArrayList;
import java.util.List;

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
        List<RuntimeException> errores = validate(direccionDTO);
        if (errores != null && !errores.isEmpty()) {
            throw new InvalidDirectionException(
                    errores.stream()
                            .map(RuntimeException::getMessage)
                            .reduce((a, b) -> a + ", " + b)
                            .orElse("")
            );
        }
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
    public List<RuntimeException> validate(DireccionDTO direccionDTO) {
        List<RuntimeException> errores = new ArrayList<>();
        RuntimeException error;
        error = new InvalidDirectionException(validatePais(direccionDTO.pais)); if (error != null) errores.add(error);
        error = new InvalidDirectionException(validateProvincia(direccionDTO.provincia)); if (error != null) errores.add(error);
        error = new InvalidDirectionException(validateLocalidad(direccionDTO.localidad)); if (error != null) errores.add(error);
        error = new InvalidDirectionException(validateCodigoPostal(direccionDTO.codigoPostal)); if (error != null) errores.add(error);
        error = new InvalidDirectionException(validateCalle(direccionDTO.calle)); if (error != null) errores.add(error);
        error = new InvalidDirectionException(validateNumero(direccionDTO.numero)); if (error != null) errores.add(error);

        return errores;
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

    private String validateCodigoPostal(Integer codigoPostal) {
        return (codigoPostal == null || codigoPostal <= 0) ? "El codigo postal es obligatorio y debe ser positivo" : null;
    }

    private String validateCalle(String calle) {
        return (calle == null || calle.trim().isEmpty()) ? "La calle es obligatoria" : null;
    }

    private String validateNumero(Integer numero) {
        return (numero == null || numero <= 0) ? "El numero es obligatorio y debe ser positivo" : null;
    }
}
