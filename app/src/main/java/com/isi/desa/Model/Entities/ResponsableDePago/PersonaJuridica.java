package com.isi.desa.Model.Entities.ResponsableDePago;

import com.isi.desa.Model.Entities.Direccion.Direccion;
import jakarta.persistence.*;

@Entity
@Table(name = "PersonaJuridica")
public class PersonaJuridica extends ResponsableDePago {
    @Column(name = "cuit", nullable = false, unique = true)
    private String cuit;
    @Column(name = "telefono", nullable = false)
    private String telefono;
    @Column(name = "razon_social", nullable = false)
    private String razonSocial;

    @OneToOne(optional = false)
    @JoinColumn(name = "id_direccion", referencedColumnName = "id_direccion")
    private Direccion direccion;

    public PersonaJuridica() {}

    public PersonaJuridica(String cuit, String telefono, String razonSocial, Direccion direccion) {
        this.cuit = cuit;
        this.telefono = telefono;
        this.razonSocial = razonSocial;
        this.direccion = direccion;
    }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public String getCuit() { return cuit; }
    public void setCuit(String cuit) { this.cuit = cuit; }

    public Direccion getDireccion() { return direccion; }
    public void setDireccion(Direccion direccion) { this.direccion = direccion; }
}