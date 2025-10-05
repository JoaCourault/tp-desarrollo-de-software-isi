package com.isi.desa.Dto.Reserva;

import com.isi.desa.Dto.Huesped.HuespedDTO;

import java.time.LocalDateTime;

public class ReservaDTO {
    public HuespedDTO titularReserva;
    public LocalDateTime fechaIngreso;
    public LocalDateTime fechaEgreso;
}
