package com.isi.desa.Service.Interfaces.Validators;

import com.isi.desa.Dto.Direccion.DireccionDTO;
import com.isi.desa.Exceptions.Direccion.InvalidDirectionException;
import com.isi.desa.Model.Entities.Direccion.Direccion;

import java.util.List;

public interface IDireccionValidator {
    InvalidDirectionException validate(DireccionDTO direccionDTO);
}
