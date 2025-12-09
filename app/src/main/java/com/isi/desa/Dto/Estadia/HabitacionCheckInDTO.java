package com.isi.desa.Dto.Estadia;

import java.time.LocalDateTime;

public class HabitacionCheckInDTO {
    public String idHabitacion;
    public LocalDateTime fechaDesde; // Check-In real
    public LocalDateTime fechaHasta; // Check-Out pactado
    public String idReservaAsociada;
}