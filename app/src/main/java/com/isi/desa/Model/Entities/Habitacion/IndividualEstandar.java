package com.isi.desa.Model.Entities.Habitacion;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("INDIVIDUAL_ESTANDAR")
public class IndividualEstandar extends Habitacion {
    public IndividualEstandar() {}

    @Override
    public void mostrarEstadoHabitaciones() {}
}
