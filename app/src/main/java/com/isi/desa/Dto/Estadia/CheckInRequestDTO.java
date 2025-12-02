package com.isi.desa.Dto.Estadia;

import com.isi.desa.Dto.Huesped.HuespedDTO;

import java.util.List;

public class CheckInRequestDTO {
    // Datos del Titular (puede venir el objeto completo o solo el ID si ya existe)
    public HuespedDTO huespedTitular;

    // IDs de los acompañantes seleccionados (Punto 8 del flujo)
    public List<String> acompanantesIds;

    // Lista de habitaciones a ocupar
    public List<HabitacionCheckInDTO> habitaciones;
}