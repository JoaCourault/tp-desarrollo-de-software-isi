package com.isi.desa.Model.Entities.Habitacion;

import com.isi.desa.Model.Enums.TipoHabitacion;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SUPERIOR_FAMILY_PLAN")
public class SuperiorFamilyPlan extends Habitacion {

    public SuperiorFamilyPlan() {
        super();
        this.setTipoHabitacion(TipoHabitacion.SUPERIOR_FAMILY_PLAN);
    }

    @Override
    public void mostrarEstadoHabitaciones() {}
}