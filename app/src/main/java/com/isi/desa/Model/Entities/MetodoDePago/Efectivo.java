package com.isi.desa.Model.Entities.MetodoDePago;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "Efectivo")
public class Efectivo extends MetodoDePago {
    @Column(name = "divisa")
    private String divisa;
    @Column(name = "tipo_de_cambio")
    private BigDecimal tipoDeCambio;

    public Efectivo() {}
    public Efectivo(String divisa) {
        this.divisa = divisa;
    }
    public String getDivisa() { return this.divisa; }
    public void setDivisa(String divisa) { this.divisa = divisa; }
    public BigDecimal getTipoDeCambio() { return this.tipoDeCambio; }
    public void setTipoDeCambio(BigDecimal tipoDeCambio) { this.tipoDeCambio = tipoDeCambio; }
}

