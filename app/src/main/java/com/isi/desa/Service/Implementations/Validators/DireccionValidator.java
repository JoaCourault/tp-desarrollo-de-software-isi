package com.isi.desa.Service.Implementations.Validators;

import com.isi.desa.Dto.Direccion.DireccionDTO;
import com.isi.desa.Model.Entities.Direccion.Direccion;
import com.isi.desa.Service.Interfaces.Validators.IDireccionValidator;
import java.util.ArrayList;
import java.util.List;

public class DireccionValidator implements IDireccionValidator {
    @Override
    public Direccion create(DireccionDTO direccionDTO) {
        List<String> errores = validate(direccionDTO);
        if (errores != null && !errores.isEmpty()) {
            throw new IllegalArgumentException(String.join(", ", errores));
        }
        return new Direccion(
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
    public List<String> validate(DireccionDTO direccionDTO) {
        List<String> errores = new ArrayList<>();
        String error;
        error = validatePais(direccionDTO.pais); if (error != null) errores.add(error);
        error = validateProvincia(direccionDTO.provincia); if (error != null) errores.add(error);
        error = validateLocalidad(direccionDTO.localidad); if (error != null) errores.add(error);
        error = validateCodigoPostal(direccionDTO.codigoPostal); if (error != null) errores.add(error);
        error = validateCalle(direccionDTO.calle); if (error != null) errores.add(error);
        error = validateNumero(direccionDTO.numero); if (error != null) errores.add(error);
        // departamento y piso pueden ser opcionales
        return errores;
    }
    @Override
    public String validatePais(String pais) {
        return (pais == null || pais.trim().isEmpty()) ? "El país es obligatorio" : null;
    }
    @Override
    public String validateProvincia(String provincia) {
        return (provincia == null || provincia.trim().isEmpty()) ? "La provincia es obligatoria" : null;
    }
    @Override
    public String validateLocalidad(String localidad) {
        return (localidad == null || localidad.trim().isEmpty()) ? "La localidad es obligatoria" : null;
    }
    @Override
    public String validateCodigoPostal(Integer codigoPostal) {
        return (codigoPostal == null || codigoPostal <= 0) ? "El código postal es obligatorio y debe ser positivo" : null;
    }
    @Override
    public String validateCalle(String calle) {
        return (calle == null || calle.trim().isEmpty()) ? "La calle es obligatoria" : null;
    }
    @Override
    public String validateNumero(Integer numero) {
        return (numero == null || numero <= 0) ? "El número es obligatorio y debe ser positivo" : null;
    }
}
