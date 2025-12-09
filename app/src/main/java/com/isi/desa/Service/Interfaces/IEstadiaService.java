package com.isi.desa.Service.Interfaces;

import com.isi.desa.Dto.Estadia.CheckInRequestDTO;

public interface IEstadiaService {
    void realizarCheckIn(CheckInRequestDTO request);
}