package com.isi.desa.Service.Interfaces;

import com.isi.desa.Dto.Reserva.CrearReservaRequestDTO;
import com.isi.desa.Dto.Reserva.HabitacionDisponibilidadDTO;

import java.time.LocalDate;
import java.util.List;

public interface IReservaService {
    void realizarReserva(CrearReservaRequestDTO request);

    List<HabitacionDisponibilidadDTO> consultarDisponibilidad(LocalDate desde, LocalDate hasta);
}
