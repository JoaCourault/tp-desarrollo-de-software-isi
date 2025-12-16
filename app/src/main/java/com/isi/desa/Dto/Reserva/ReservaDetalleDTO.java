package com.isi.desa.Dto.Reserva;

import java.time.LocalDate;

public class ReservaDetalleDTO {
    public String idHabitacion;
    public LocalDate fechaDesde;
    public LocalDate fechaHasta;

    public ReservaDetalleDTO() {}

    // Getters y Setters
    public String getIdHabitacion() { return idHabitacion; }
    public void setIdHabitacion(String idHabitacion) { this.idHabitacion = idHabitacion; }

    public LocalDate getFechaDesde() { return fechaDesde; }
    public void setFechaDesde(LocalDate fechaDesde) { this.fechaDesde = fechaDesde; }

    public LocalDate getFechaHasta() { return fechaHasta; }
    public void setFechaHasta(LocalDate fechaHasta) { this.fechaHasta = fechaHasta; }
}