package com.isi.desa.Model.Entities.Habitacion;

import com.isi.desa.Model.Enums.TipoHabitacion;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SUITE_DOBLE")
public class SuiteDoble extends Habitacion {

    public SuiteDoble() {
        super();
        this.setTipoHabitacion(TipoHabitacion.SUITE_DOBLE);
    }

}