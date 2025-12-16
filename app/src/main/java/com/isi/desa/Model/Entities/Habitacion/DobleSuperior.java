package com.isi.desa.Model.Entities.Habitacion;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("DOBLE_SUPERIOR")
public class DobleSuperior extends Habitacion {

    @Column(name = "qcamindividual")
    private Integer cantidadCamasIndividual;
    @Column(name = "qcamdobles")
    private Integer cantidadCamasDobles;
    @Column(name = "qcamkingsize")
    private Integer cantidadCamasKingSize;

    public DobleSuperior() {}

    // Getters y Setters
    public Integer getCantidadCamasIndividual() { return cantidadCamasIndividual; }
    public void setCantidadCamasIndividual(Integer cantidadCamasIndividual) { this.cantidadCamasIndividual = cantidadCamasIndividual; }
    public Integer getCantidadCamasDobles() { return cantidadCamasDobles; }
    public void setCantidadCamasDobles(Integer cantidadCamasDobles) { this.cantidadCamasDobles = cantidadCamasDobles; }
    public Integer getCantidadCamasKingSize() { return cantidadCamasKingSize; }
    public void setCantidadCamasKingSize(Integer cantidadCamasKingSize) { this.cantidadCamasKingSize = cantidadCamasKingSize; }
}