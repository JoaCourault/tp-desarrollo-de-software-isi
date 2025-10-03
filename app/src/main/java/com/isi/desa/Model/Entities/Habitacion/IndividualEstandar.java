package com.isi.desa.Model.Entities.Habitacion;

public class IndividualEstandar extends Habitacion {
    private Integer cantidadCamasIndividual;

    public IndividualEstandar() {}
    public IndividualEstandar(Integer cantidadCamasIndividual) {
        this.cantidadCamasIndividual = cantidadCamasIndividual;
    }

    @Override
    public void mostrarEstadoHabitaciones() {}

    public Integer getCantidadCamasIndividual() { return cantidadCamasIndividual; }
    public void setCantidadCamasIndividual(Integer cantidadCamasIndividual) { this.cantidadCamasIndividual = cantidadCamasIndividual; }
}
