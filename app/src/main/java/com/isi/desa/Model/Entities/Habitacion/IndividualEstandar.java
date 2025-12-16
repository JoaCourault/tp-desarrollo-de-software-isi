package com.isi.desa.Model.Entities.Habitacion;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("INDIVIDUAL_ESTANDAR") // Valor exacto en la BD columna tipo_habitacion
public class IndividualEstandar extends Habitacion {

    @Column(name = "qcamindividual")
    private Integer cantidadCamasIndividual;

    public IndividualEstandar() {}

    public Integer getCantidadCamasIndividual() { return cantidadCamasIndividual; }
    public void setCantidadCamasIndividual(Integer cantidadCamasIndividual) { this.cantidadCamasIndividual = cantidadCamasIndividual; }
}