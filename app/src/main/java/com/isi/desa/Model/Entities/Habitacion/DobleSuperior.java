package com.isi.desa.Model.Entities.Habitacion;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("DOBLE_SUPERIOR")
public class DobleSuperior extends Habitacion {

    public DobleSuperior() {}

    @Override
    public void mostrarEstadoHabitaciones() {}
}
