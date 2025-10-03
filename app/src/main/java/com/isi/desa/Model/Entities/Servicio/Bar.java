package com.isi.desa.Model.Entities.Servicio;

public class Bar extends Servicio {
    private String detalle;

    public Bar() {}

    public Bar(String detalle) {
        this.detalle = detalle;
    }

    @Override
    public void cargarServicio() {}

    @Override
    public java.math.BigDecimal calcularPrecio() { return null; }

    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }
}
