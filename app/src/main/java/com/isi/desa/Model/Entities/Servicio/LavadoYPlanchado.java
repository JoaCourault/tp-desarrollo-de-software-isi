package com.isi.desa.Model.Entities.Servicio;

import java.math.BigDecimal;

public class LavadoYPlanchado extends Servicio {
    private Integer cantidadPrendas;

    public LavadoYPlanchado() {}

    public LavadoYPlanchado(Integer cantidadPrendas) {
        this.cantidadPrendas = cantidadPrendas;
    }

    @Override
    public void cargarServicio() {}

    @Override
    public BigDecimal calcularPrecio() { return null; }

    public Integer getCantidadPrendas() { return cantidadPrendas; }
    public void setCantidadPrendas(Integer cantidadPrendas) { this.cantidadPrendas = cantidadPrendas; }
}
