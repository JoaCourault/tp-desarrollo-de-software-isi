package com.isi.desa.Model.Entities.Servicio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

public abstract class Servicio {
    protected LocalDateTime fecha;
    protected BigDecimal precio;

    public abstract void cargarServicio();
    public abstract BigDecimal calcularPrecio();

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
}
