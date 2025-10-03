package com.isi.desa.Model.Entities.ResponsableDePago;

import com.isi.desa.Model.Entities.Direccion.Direccion;

public class PersonaJuridica extends ResponsableDePago {
    private String telefono;
    private String razonSocial;
    private Direccion direccion;

    public PersonaJuridica() {}

    public PersonaJuridica(String telefono, String razonSocial, Direccion direccion) {
        this.telefono = telefono;
        this.razonSocial = razonSocial;
        this.direccion = direccion;
    }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public Direccion getDireccion() { return direccion; }
    public void setDireccion(Direccion direccion) { this.direccion = direccion; }
}
