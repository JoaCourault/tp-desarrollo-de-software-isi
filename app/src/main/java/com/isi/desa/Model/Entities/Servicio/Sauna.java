package com.isi.desa.Model.Entities.Servicio;

import java.math.BigDecimal;

public class Sauna extends Servicio {
    private Integer cantidadPersonas;

    public Sauna() {}

    public Sauna(Integer cantidadPersonas) { this.cantidadPersonas = cantidadPersonas; }

    @Override
    public void cargarServicio() {}

    @Override
    public BigDecimal calcularPrecio() { return null; }

    public Integer getCantidadPersonas() { return cantidadPersonas; }
    public void setCantidadPersonas(Integer cantidadPersonas) { this.cantidadPersonas = cantidadPersonas; }
}