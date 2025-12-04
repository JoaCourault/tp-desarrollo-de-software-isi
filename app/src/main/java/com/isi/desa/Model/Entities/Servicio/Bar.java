package com.isi.desa.Model.Entities.Servicio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import java.math.BigDecimal;

@Entity
public class Bar extends Servicio {

    @Column(name = "detalle")
    private String detalle;

    public Bar() {}
    public Bar(String detalle) { this.detalle = detalle; }

    @Override
    public void cargarServicio() {}

    @Override
    public BigDecimal calcularPrecio() { return precio; }

    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }
}
