package com.isi.desa.Model.Entities.Reserva;

import com.isi.desa.Model.Entities.Huesped.Huesped;
import java.util.Date;

public class Reserva {
    private Huesped TitularReserva;
    private Date FechaIngreso;
    private Date FechaEgreso;

    public Reserva() {}

    public Reserva(Huesped TitularReserva, Date FechaIngreso, Date FechaEgreso) {
        this.TitularReserva = TitularReserva;
        this.FechaIngreso = FechaIngreso;
        this.FechaEgreso = FechaEgreso;
    }

    public Huesped GetTitularReserva() { return TitularReserva; }
    public void SetTitularReserva(Huesped TitularReserva) { this.TitularReserva = TitularReserva; }
    public Date GetFechaIngreso() { return FechaIngreso; }
    public void SetFechaIngreso(Date FechaIngreso) { this.FechaIngreso = FechaIngreso; }
    public Date GetFechaEgreso() { return FechaEgreso; }
    public void SetFechaEgreso(Date FechaEgreso) { this.FechaEgreso = FechaEgreso; }
}
