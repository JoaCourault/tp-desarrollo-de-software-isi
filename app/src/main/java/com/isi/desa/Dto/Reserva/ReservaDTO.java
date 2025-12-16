package com.isi.desa.Dto.Reserva;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReservaDTO {
    public String idReserva;

    // Estos nombres coinciden con lo que envía el FRONT (Json)
    public String nombreCliente;
    public String apellidoCliente;
    public String telefonoCliente;
    public String emailCliente;

    public String estado;

    public LocalDate fechaDesde;   // Planificado
    public LocalDate fechaHasta;   // Planificado

    // Actualizado a LocalDateTime para coincidir con BBDD TIMESTAMP
    public LocalDateTime fechaIngreso; // Real/Timestamp
    public LocalDateTime fechaEgreso;  // Real/Timestamp

    public String idHabitacion;
}