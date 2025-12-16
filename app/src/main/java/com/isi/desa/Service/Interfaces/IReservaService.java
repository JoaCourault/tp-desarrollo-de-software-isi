package com.isi.desa.Service.Interfaces;

import com.isi.desa.Dto.Reserva.CrearReservaRequestDTO;
import com.isi.desa.Dto.Reserva.HabitacionDisponibilidadDTO;
import com.isi.desa.Dto.Reserva.ReservaListadoDTO;

import java.time.LocalDate;
import java.util.List;

public interface IReservaService {
    void realizarReserva(CrearReservaRequestDTO request);

    List<HabitacionDisponibilidadDTO> consultarDisponibilidad(LocalDate desde, LocalDate hasta, String tipoHabitacion);

    List<ReservaListadoDTO> buscarParaCancelar(String apellido, String nombre);
    void cancelarReservas(List<String> idsReservas);
}