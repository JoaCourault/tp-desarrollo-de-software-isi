package com.isi.desa.Model.Entities.Habitacion;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("Suite Doble")
public class SuiteDoble extends HabitacionEntity {

    @Column(name = "qcamdobles")
    private Integer cantidadCamasDobles;

    public SuiteDoble() {}

    public Integer getCantidadCamasDobles() { return cantidadCamasDobles; }
    public void setCantidadCamasDobles(Integer cantidadCamasDobles) { this.cantidadCamasDobles = cantidadCamasDobles; }
}