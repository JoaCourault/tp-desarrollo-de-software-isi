package com.isi.desa.Model.Entities.Habitacion;

import com.isi.desa.Model.Enums.TipoHabitacion;

public class IndividualEstandar extends Habitacion {
    public IndividualEstandar() {
        this.setTipoHabitacion(TipoHabitacion.INDIVIDUAL_ESTANDAR);
    }

    @Override
    public void mostrarEstadoHabitaciones() {}
}
