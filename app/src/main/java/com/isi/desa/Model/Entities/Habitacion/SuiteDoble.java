package com.isi.desa.Model.Entities.Habitacion;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("SUITE_DOBLE")
public class SuiteDoble extends Habitacion {

    @Column(name = "qcamdobles")
    private Integer cantidadCamasDobles;

    public SuiteDoble() {}

    public Integer getCantidadCamasDobles() { return cantidadCamasDobles; }
    public void setCantidadCamasDobles(Integer cantidadCamasDobles) { this.cantidadCamasDobles = cantidadCamasDobles; }
}