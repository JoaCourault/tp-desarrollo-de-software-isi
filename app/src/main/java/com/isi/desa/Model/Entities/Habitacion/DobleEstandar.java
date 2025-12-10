package com.isi.desa.Model.Entities.Habitacion;

import com.isi.desa.Model.Enums.TipoHabitacion;
import jakarta.persistence.Column;

public class DobleEstandar extends Habitacion {

    public DobleEstandar() {
        this.setTipoHabitacion(TipoHabitacion.DOBLE_ESTANDAR);
    }

    @Override
    public void mostrarEstadoHabitaciones() {}
}
