package com.isi.desa.Model.Entities.Habitacion;

import com.isi.desa.Model.Enums.TipoHabitacion;

public class DobleSuperior extends Habitacion {

    public DobleSuperior() {
        this.setTipoHabitacion(TipoHabitacion.DOBLE_SUPERIOR);
    }

    @Override
    public void mostrarEstadoHabitaciones() {}
}
