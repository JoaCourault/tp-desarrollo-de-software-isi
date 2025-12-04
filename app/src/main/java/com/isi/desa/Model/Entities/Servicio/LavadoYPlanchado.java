package com.isi.desa.Model.Entities.Servicio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import java.math.BigDecimal;

@Entity
public class LavadoYPlanchado extends Servicio {

    @Column(name = "cantidad_prendas")
    private Integer cantidadPrendas;

    public LavadoYPlanchado() {}
    public LavadoYPlanchado(Integer cantidadPrendas) { this.cantidadPrendas = cantidadPrendas; }

    @Override
    public void cargarServicio() {}

    @Override
    public BigDecimal calcularPrecio() { return precio; }

    public Integer getCantidadPrendas() { return cantidadPrendas; }
    public void setCantidadPrendas(Integer cantidadPrendas) { this.cantidadPrendas = cantidadPrendas; }
}
