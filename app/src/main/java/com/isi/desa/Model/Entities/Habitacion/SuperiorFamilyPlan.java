package com.isi.desa.Model.Entities.Habitacion;

import com.isi.desa.Model.Enums.TipoHabitacion;
import jakarta.persistence.*;

@Entity
@DiscriminatorValue("SUPERIOR_FAMILY_PLAN")
public class SuperiorFamilyPlan extends Habitacion {
    public SuperiorFamilyPlan() {}

    @Override
    public void mostrarEstadoHabitaciones() {}
}
