package com.isi.desa.Model.Entities.Habitacion;

public class DobleSuperior extends Habitacion {
    private Integer cantidadCamasIndividual;
    private Integer cantidadCamasDobles;
    private Integer cantidadCamasKingSize;

    public DobleSuperior() {}

    public DobleSuperior(Integer cantidadCamasIndividual, Integer cantidadCamasDobles, Integer cantidadCamasKingSize) {
        this.cantidadCamasIndividual = cantidadCamasIndividual;
        this.cantidadCamasDobles = cantidadCamasDobles;
        this.cantidadCamasKingSize = cantidadCamasKingSize;
    }

    @Override
    public void mostrarEstadoHabitaciones() {}

    public Integer getCantidadCamasIndividual() { return cantidadCamasIndividual; }
    public void setCantidadCamasIndividual(Integer cantidadCamasIndividual) { this.cantidadCamasIndividual = cantidadCamasIndividual; }

    public Integer getCantidadCamasDobles() { return cantidadCamasDobles; }
    public void setCantidadCamasDobles(Integer cantidadCamasDobles) { this.cantidadCamasDobles = cantidadCamasDobles; }

    public Integer getCantidadCamasKingSize() { return cantidadCamasKingSize; }
    public void setCantidadCamasKingSize(Integer cantidadCamasKingSize) { this.cantidadCamasKingSize = cantidadCamasKingSize; }
}
