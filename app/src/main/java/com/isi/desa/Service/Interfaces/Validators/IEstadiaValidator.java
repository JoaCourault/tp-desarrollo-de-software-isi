package com.isi.desa.Service.Interfaces.Validators;

import com.isi.desa.Dto.Estadia.EstadiaDTO;

public interface IEstadiaValidator {
    RuntimeException validateCreate(EstadiaDTO dto);
    RuntimeException validateUpdate(EstadiaDTO dto);
}