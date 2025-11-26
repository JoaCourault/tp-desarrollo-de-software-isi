package com.isi.desa.Service.Interfaces;

import com.isi.desa.Dto.Reserva.CrearReservaRequestDTO;

public interface IReservaService {
    // Solo definimos el contrato del método
    void crear(CrearReservaRequestDTO request);
}