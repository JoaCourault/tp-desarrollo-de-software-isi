package com.isi.desa.Service.Interfaces.Validators;

import com.isi.desa.Dto.Reserva.CrearReservaRequestDTO;

public interface IReservaValidator {
    void validateCreate(CrearReservaRequestDTO request);
}