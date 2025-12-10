package com.isi.desa.Dto.Reserva;


import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReservaDTO {
    public String idReserva;
    public String nombreCliente;
    public String apellidoCliente;
    public String telefonoCliente;
    public String emailCliente;

    public String estado;

    public LocalDate fechaDesde;
    public LocalDate fechaHasta;
    public LocalDate fechaIngreso;
    public LocalDate fechaEgreso;

    public String idHabitacion;
}

