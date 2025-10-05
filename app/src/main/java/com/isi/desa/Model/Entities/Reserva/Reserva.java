package com.isi.desa.Model.Entities.Reserva;

import com.isi.desa.Model.Entities.Huesped.Huesped;

import java.time.LocalDateTime;

public class Reserva {
    private Huesped titularReserva;
    private LocalDateTime fechaIngreso;
    private LocalDateTime fechaEgreso;

    public Reserva() {}

    public Reserva(Huesped titularReserva, LocalDateTime fechaIngreso, LocalDateTime fechaEgreso) {
        this.titularReserva = titularReserva;
        this.fechaIngreso = fechaIngreso;
        this.fechaEgreso = fechaEgreso;
    }

    public Huesped getTitularReserva() { return this.titularReserva; }
    public void setTitularReserva(Huesped titularReserva) { this.titularReserva = titularReserva; }
    public LocalDateTime getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDateTime fechaIngreso) { this.fechaIngreso = fechaIngreso; }
    public LocalDateTime getFechaEgreso() { return fechaEgreso; }
    public void setFechaEgreso(LocalDateTime fechaEgreso) { this.fechaEgreso = fechaEgreso; }
}
