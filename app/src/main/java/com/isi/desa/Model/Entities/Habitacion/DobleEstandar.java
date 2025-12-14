package com.isi.desa.Model.Entities.Habitacion;

import com.isi.desa.Model.Enums.TipoHabitacion;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("DOBLE_ESTANDAR")
public class DobleEstandar extends Habitacion {

    public DobleEstandar() {}

    @Override
    public void mostrarEstadoHabitaciones() {}
}
