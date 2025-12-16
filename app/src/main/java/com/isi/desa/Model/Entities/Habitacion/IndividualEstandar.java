package com.isi.desa.Model.Entities.Habitacion;

import com.isi.desa.Model.Enums.TipoHabitacion;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("INDIVIDUAL_ESTANDAR")
public class IndividualEstandar extends Habitacion {

    public IndividualEstandar() {
        super();
        this.setTipoHabitacion(TipoHabitacion.INDIVIDUAL_ESTANDAR);
    }

}