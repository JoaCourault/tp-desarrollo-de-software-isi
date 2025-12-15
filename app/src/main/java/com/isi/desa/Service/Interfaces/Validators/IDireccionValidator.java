package com.isi.desa.Service.Interfaces.Validators;

import com.isi.desa.Dto.Direccion.DireccionDTO;

public interface IDireccionValidator {

    RuntimeException validate(DireccionDTO direccionDTO);
}