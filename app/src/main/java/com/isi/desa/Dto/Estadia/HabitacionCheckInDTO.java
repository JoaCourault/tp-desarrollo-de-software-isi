package com.isi.desa.Dto.Estadia;

import java.time.LocalDateTime;

public class HabitacionCheckInDTO {
    public String idHabitacion;
    public LocalDateTime fechaDesde; // Check-In real
    public LocalDateTime fechaHasta; // Check-Out pactado
    // Opcional: ID de reserva si viene de una "Ocupar Igual" sobre una reserva existente
    public String idReservaAsociada;
}