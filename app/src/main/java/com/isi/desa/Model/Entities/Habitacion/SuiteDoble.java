package com.isi.desa.Model.Entities.Habitacion;

public class SuiteDoble extends Habitacion {
    private Integer cantidadCamasDobles;

    public SuiteDoble() {}

    public SuiteDoble(Integer cantidadCamasDobles) {
        this.cantidadCamasDobles = cantidadCamasDobles;
    }

    @Override
    public void mostrarEstadoHabitaciones() {}

    public Integer getCantidadCamasDobles() { return cantidadCamasDobles; }
    public void setCantidadCamasDobles(Integer cantidadCamasDobles) { this.cantidadCamasDobles = cantidadCamasDobles; }
}
