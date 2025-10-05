package com.isi.desa.Model.Entities.Huesped;

import com.isi.desa.Model.Entities.Tipodocumento.TipoDocumento;
import com.isi.desa.Model.Entities.Direccion.Direccion;
import java.util.Date;

public class Huesped {
    private String nombre;
    private String apellido;
    private TipoDocumento tipoDocumento;
    private String numDoc;
    private String posicionIva;
    private String cuit;
    private Date fechaNacimiento;
    private String telefono;
    private String email;
    private String ocupacion;
    private String nacionalidad;
    private Direccion direccion;

    public Huesped() {}

    public Huesped(String nombre, String apellido, TipoDocumento tipoDocumento, String numDoc, String posicionIva, String cuit, Date fechaNacimiento, String telefono, String email, String ocupacion, String nacionalidad, Direccion direccion) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.tipoDocumento = tipoDocumento;
        this.numDoc = numDoc;
        this.posicionIva = posicionIva;
        this.cuit = cuit;
        this.fechaNacimiento = fechaNacimiento;
        this.telefono = telefono;
        this.email = email;
        this.ocupacion = ocupacion;
        this.nacionalidad = nacionalidad;
        this.direccion = direccion;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public TipoDocumento getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(TipoDocumento tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getNumDoc() { return numDoc; }
    public void setNumDoc(String numDoc) { this.numDoc = numDoc; }

    public String getPosicionIva() { return posicionIva; }
    public void setPosicionIva(String posicionIva) { this.posicionIva = posicionIva; }

    public String getCuit() { return cuit; }
    public void setCuit(String cuit) { this.cuit = cuit; }

    public Date getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(Date fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getOcupacion() { return ocupacion; }
    public void setOcupacion(String ocupacion) { this.ocupacion = ocupacion; }

    public String getNacionalidad() { return nacionalidad; }
    public void setNacionalidad(String nacionalidad) { this.nacionalidad = nacionalidad; }

    public Direccion getDireccion() { return direccion; }
    public void setDireccion(Direccion direccion) { this.direccion = direccion; }
}