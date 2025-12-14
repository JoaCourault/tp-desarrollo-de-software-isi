package com.isi.desa.Model.Entities.Servicio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "Sauna")
public class Sauna extends Servicio {

    @Column(name = "cantidad_personas")
    private Integer cantidadPersonas;

    public Sauna() {
    }

    public Sauna(Integer cantidadPersonas) {
        this.cantidadPersonas = cantidadPersonas;
    }

    @Override
    public void cargarServicio() {
    }

    @Override
    public BigDecimal calcularPrecio() {
        return precio;
    }

    public Integer getCantidadPersonas() {
        return cantidadPersonas;
    }

    public void setCantidadPersonas(Integer cantidadPersonas) {
        this.cantidadPersonas = cantidadPersonas;
    }
}
