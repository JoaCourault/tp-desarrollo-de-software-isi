package com.isi.desa.Model.Entities.Servicio;

import java.math.BigDecimal;
import java.time.LocalTime;

public abstract class Servicio {
    protected LocalTime fecha;
    protected BigDecimal precio;

    public abstract void cargarServicio();
    public abstract BigDecimal calcularPrecio();

    public LocalTime getFecha() { return fecha; }
    public void setFecha(LocalTime fecha) { this.fecha = fecha; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
}
