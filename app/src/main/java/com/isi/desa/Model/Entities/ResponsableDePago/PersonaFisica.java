package com.isi.desa.Model.Entities.ResponsableDePago;

import com.isi.desa.Model.Entities.Huesped.Huesped;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "PersonaFisica")
public class PersonaFisica extends ResponsableDePago {
    @OneToOne
    @JoinColumn(name = "id_huesped", referencedColumnName = "id_huesped")
    private Huesped huesped;

    public PersonaFisica() {}

    public PersonaFisica(Huesped huesped) {
        this.huesped = huesped;
    }

    public Huesped getHuesped() { return huesped; }
    public void setHuesped(Huesped huesped) { this.huesped = huesped; }
}