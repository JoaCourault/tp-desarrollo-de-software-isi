package com.isi.desa.Model.Entities.Habitacion;


import com.isi.desa.Model.Enums.TipoHabitacion;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("DOBLE_SUPERIOR")
public class DobleSuperior extends Habitacion {


    public DobleSuperior() {
        super();
        this.setTipoHabitacion(TipoHabitacion.DOBLE_SUPERIOR);
    }

}