package com.isi.desa.Model.Entities.Direccion;

import jakarta.persistence.*;

@Entity
@Table(name = "direccion")
public class Direccion {

    @Id
    @Column(name = "id_direccion", nullable = false)
    private String idDireccion;

    @Column(name = "calle")
    private String calle;

    @Column(name = "numero")
    private Integer numero;

    @Column(name = "depto")
    private String departamento;

    @Column(name = "piso")
    private Integer piso;

    @Column(name = "cp")
    private Integer cp;

    @Column(name = "localidad")
    private String localidad;

    @Column(name = "provincia")
    private String provincia;

    @Column(name = "pais")
    private String pais;

    public Direccion() {
    }

    // Getters & Setters

    public String getIdDireccion() {
        return idDireccion;
    }

    public void setIdDireccion(String idDireccion) {
        this.idDireccion = idDireccion;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public Integer getPiso() {
        return piso;
    }

    public void setPiso(Integer piso) {
        this.piso = piso;
    }

    public Integer getCp() {
        return cp;
    }

    public void setCp(Integer cp) {
        this.cp = cp;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }
}
