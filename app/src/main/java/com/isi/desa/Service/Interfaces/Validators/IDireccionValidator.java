package com.isi.desa.Service.Interfaces.Validators;

import com.isi.desa.Dto.Direccion.DireccionDTO;
import com.isi.desa.Model.Entities.Direccion.Direccion;

import java.util.List;

public interface IDireccionValidator {
    Direccion create(DireccionDTO direccionDTO);
    List<String> validate(DireccionDTO direccionDTO);
    String validatePais(String pais);
    String validateProvincia(String provincia);
    String validateLocalidad(String localidad);
    String validateCodigoPostal(Integer codigoPostal);
    String validateCalle(String calle);
    String validateNumero(Integer numero);
}
