package com.isi.desa.Model.Entities.Habitacion;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("Individual Estandar") // Valor exacto en la BD columna tipo_habitacion
public class IndividualEstandar extends HabitacionEntity {

    @Column(name = "qcamindividual")
    private Integer cantidadCamasIndividual;

    public IndividualEstandar() {}

    public Integer getCantidadCamasIndividual() { return cantidadCamasIndividual; }
    public void setCantidadCamasIndividual(Integer cantidadCamasIndividual) { this.cantidadCamasIndividual = cantidadCamasIndividual; }
}