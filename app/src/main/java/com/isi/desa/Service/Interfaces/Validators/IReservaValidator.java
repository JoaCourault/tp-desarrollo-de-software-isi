package com.isi.desa.Service.Interfaces.Validators;

import com.isi.desa.Dto.Reserva.CrearReservaRequestDTO;

public interface IReservaValidator {
    // Si hay error lanzará una excepción, si no, no hace nada.
    void validateCreate(CrearReservaRequestDTO request);
}