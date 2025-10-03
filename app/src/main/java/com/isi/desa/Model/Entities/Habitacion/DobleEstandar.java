package com.isi.desa.Model.Entities.Habitacion;

public class DobleEstandar extends Habitacion {
    private Integer cantidadCamasIndividual;
    private Integer cantidadCamasDobles;

    public DobleEstandar() {}

    public DobleEstandar(Integer cantidadCamasIndividual, Integer cantidadCamasDobles) {
        this.cantidadCamasIndividual = cantidadCamasIndividual;
        this.cantidadCamasDobles = cantidadCamasDobles;
    }

    @Override
    public void mostrarEstadoHabitaciones() {}

    public Integer getCantidadCamasIndividual() { return cantidadCamasIndividual; }
    public void setCantidadCamasIndividual(Integer cantidadCamasIndividual) { this.cantidadCamasIndividual = cantidadCamasIndividual; }

    public Integer getCantidadCamasDobles() { return cantidadCamasDobles; }
    public void setCantidadCamasDobles(Integer cantidadCamasDobles) { this.cantidadCamasDobles = cantidadCamasDobles; }
}
