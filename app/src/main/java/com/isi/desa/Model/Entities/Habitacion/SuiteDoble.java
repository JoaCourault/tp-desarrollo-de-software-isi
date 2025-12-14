package com.isi.desa.Model.Entities.Habitacion;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SUITE_DOBLE")
public class SuiteDoble extends Habitacion {
    public SuiteDoble() {}

    @Override
    public void mostrarEstadoHabitaciones() {}
}
