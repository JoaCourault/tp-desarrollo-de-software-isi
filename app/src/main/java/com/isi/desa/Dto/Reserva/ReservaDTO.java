package com.isi.desa.Dto.Reserva;

import com.isi.desa.Dto.Habitacion.HabitacionDTO;
import com.isi.desa.Dto.Huesped.HuespedDTO;

import java.time.LocalDateTime;

public class ReservaDTO {
    public String idReserva;
    public LocalDateTime fechaIngreso;
    public LocalDateTime fechaEgreso;
    public String nombreHuesped;
    public String apellidoHuesped;
    public String telefonoHuesped;
    public HabitacionDTO habitacion;
}
