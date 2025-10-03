package com.isi.desa.Model.Entities.ResponsableDePago;

import com.isi.desa.Model.Entities.Huesped.Huesped;

public class PersonaFisica extends ResponsableDePago {
    private Huesped huesped;

    public PersonaFisica() {}

    public PersonaFisica(Huesped huesped) {
        this.huesped = huesped;
    }

    public Huesped getHuesped() { return huesped; }
    public void setHuesped(Huesped huesped) { this.huesped = huesped; }
}
