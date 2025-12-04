package com.isi.desa.Model.Entities.ResponsableDePago;

import com.isi.desa.Model.Entities.Direccion.Direccion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;


@Entity
@Table(name = "ResponsableDePago")
public class PersonaJuridica extends ResponsableDePago {
    @Column(name = "cuit", nullable = false, unique = true)
    private String cuit;
    @Column(name = "telefono", nullable = false)
    private String telefono;
    @Column(name = "razon_social", nullable = false)
    private String razonSocial;

    public PersonaJuridica() {}

    public PersonaJuridica(String cuit, String telefono, String razonSocial, Direccion direccion) {
        this.cuit = cuit;
        this.telefono = telefono;
        this.razonSocial = razonSocial;
    }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public String getCuit() { return cuit; }
    public void setCuit(String cuit) { this.cuit = cuit; }
}
