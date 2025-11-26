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

    @Override
    public Direccion create(DireccionDTO dto) {
        InvalidDirectionException validation = validate(dto);
        if (validation != null) throw validation;

        Direccion d = new Direccion();
        d.setIdDireccion(dto.id);
        d.setCalle(dto.calle);
        d.setNumero(dto.numero);
        d.setDepartamento(dto.departamento);
        d.setPiso(dto.piso);
        d.setCp(dto.cp);
        d.setLocalidad(dto.localidad);
        d.setProvincia(dto.provincia);
        d.setPais(dto.pais);

        return d;
    }

    @Override
    public InvalidDirectionException validate(DireccionDTO dto) {
        if (dto == null)
            return new InvalidDirectionException("La dirección no puede ser nula");

        List<String> errores = new ArrayList<>();

        if (isBlank(dto.pais)) errores.add("El país es obligatorio");
        if (isBlank(dto.provincia)) errores.add("La provincia es obligatoria");
        if (isBlank(dto.localidad)) errores.add("La localidad es obligatoria");
        if (isBlank(dto.calle)) errores.add("La calle es obligatoria");

        if (dto.numero == null || dto.numero <= 0)
            errores.add("El número es obligatorio y debe ser positivo");

        if (dto.cp == null || dto.cp <= 0)
            errores.add("El código postal es obligatorio y debe ser positivo");

        if (!errores.isEmpty()) {
            return new InvalidDirectionException(String.join(", ", errores));
        }

        return null;
    }

    private boolean isBlank(String s) {
        return (s == null || s.trim().isEmpty());
    }
}
