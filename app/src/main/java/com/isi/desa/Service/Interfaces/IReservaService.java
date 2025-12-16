package com.isi.desa.Service.Interfaces;

import com.isi.desa.Dto.Habitacion.HabitacionDisponibilidadDTO;
import com.isi.desa.Dto.Reserva.CrearReservaRequestDTO;

import java.time.LocalDate;
import java.util.List;

public interface IReservaService {
    void crear(CrearReservaRequestDTO request);
    List<HabitacionDisponibilidadDTO> obtenerDisponibilidad(LocalDate desde, LocalDate hasta, String tipoHabitacion) ;
    }