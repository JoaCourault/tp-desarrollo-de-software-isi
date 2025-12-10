package com.isi.desa.Model.Entities.Habitacion;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("Superior Family Plan") // Valor que se guardará en la columna 'tipo_habitacion'
public class SuperiorFamilyPlan extends HabitacionEntity {

    @Column(name = "qcamindividual")
    private Integer cantidadCamasIndividual;

    @Column(name = "qcamdobles")
    private Integer cantidadCamasDobles;

    public SuperiorFamilyPlan() {}

    public SuperiorFamilyPlan(Integer cantidadCamasIndividual, Integer cantidadCamasDobles) {
        this.cantidadCamasIndividual = cantidadCamasIndividual;
        this.cantidadCamasDobles = cantidadCamasDobles;
    }

    // --- Getters y Setters ---

    public Integer getCantidadCamasIndividual() { return cantidadCamasIndividual; }
    public void setCantidadCamasIndividual(Integer cantidadCamasIndividual) { this.cantidadCamasIndividual = cantidadCamasIndividual; }

    public Integer getCantidadCamasDobles() { return cantidadCamasDobles; }
    public void setCantidadCamasDobles(Integer cantidadCamasDobles) { this.cantidadCamasDobles = cantidadCamasDobles; }
}