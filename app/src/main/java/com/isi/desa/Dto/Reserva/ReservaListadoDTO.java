package com.isi.desa.Dto.Reserva;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReservaListadoDTO {
    public String idReserva;
    public String apellidoHuesped;
    public String nombreHuesped;
    public Integer numeroHabitacion;
    public String tipoHabitacion; // "Detalles" en tu entidad Habitacion
    public LocalDate fechaIngreso;
    public LocalDate fechaEgreso;

    // Constructor vacío
    public ReservaListadoDTO() {}

    // Constructor con campos para facilitar el mapeo
    public ReservaListadoDTO(String idReserva, String apellidoHuesped, String nombreHuesped,
                             Integer numeroHabitacion, String tipoHabitacion,
                             LocalDate fechaIngreso, LocalDate fechaEgreso) {
        this.idReserva = idReserva;
        this.apellidoHuesped = apellidoHuesped;
        this.nombreHuesped = nombreHuesped;
        this.numeroHabitacion = numeroHabitacion;
        this.tipoHabitacion = tipoHabitacion;
        this.fechaIngreso = fechaIngreso;
        this.fechaEgreso = fechaEgreso;
    }
}