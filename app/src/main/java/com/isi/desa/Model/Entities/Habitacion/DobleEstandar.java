package com.isi.desa.Model.Entities.Habitacion;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("DOBLE_ESTANDAR") // Este valor debe coincidir con el case del Mapper
public class DobleEstandar extends Habitacion {

    @Column(name = "qcamindividual")
    private Integer cantidadCamasIndividual;

    @Column(name = "qcamdobles")
    private Integer cantidadCamasDobles;

    public DobleEstandar() {}

    public DobleEstandar(Integer cantidadCamasIndividual, Integer cantidadCamasDobles) {
        this.cantidadCamasIndividual = cantidadCamasIndividual;
        this.cantidadCamasDobles = cantidadCamasDobles;
    }

    // --- Getters y Setters ---

    public Integer getCantidadCamasIndividual() {
        return cantidadCamasIndividual;
    }

    public void setCantidadCamasIndividual(Integer cantidadCamasIndividual) {
        this.cantidadCamasIndividual = cantidadCamasIndividual;
    }

    public Integer getCantidadCamasDobles() {
        return cantidadCamasDobles;
    }

    public void setCantidadCamasDobles(Integer cantidadCamasDobles) {
        this.cantidadCamasDobles = cantidadCamasDobles;
    }
}