package com.isi.desa.Model.Entities.Habitacion;

import com.isi.desa.Model.Enums.TipoHabitacion;

public class SuperiorFamilyPlan extends Habitacion {
    public SuperiorFamilyPlan() {
        this.setTipoHabitacion(TipoHabitacion.SUPERIOR_FAMILY_PLAN);
    }

    @Override
    public void mostrarEstadoHabitaciones() {}
}
