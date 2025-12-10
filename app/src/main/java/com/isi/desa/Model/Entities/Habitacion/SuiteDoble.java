package com.isi.desa.Model.Entities.Habitacion;

import com.isi.desa.Model.Enums.TipoHabitacion;

public class SuiteDoble extends Habitacion {
    public SuiteDoble() {
        this.setTipoHabitacion(TipoHabitacion.SUITE_DOBLE);
    }

    @Override
    public void mostrarEstadoHabitaciones() {}
}
