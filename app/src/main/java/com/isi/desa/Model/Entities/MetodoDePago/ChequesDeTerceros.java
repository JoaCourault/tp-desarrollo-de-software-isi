package com.isi.desa.Model.Entities.MetodoDePago;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ChequesDeTerceros extends MetodoDePago {
    private Integer numCheque;
    private String emisor;
    private String banco;
    private BigDecimal monto;
    private LocalDate fecha;

    public ChequesDeTerceros() {}

    public ChequesDeTerceros(Integer numCheque, String emisor, String banco, BigDecimal monto, LocalDate fecha) {
        this.numCheque = numCheque;
        this.emisor = emisor;
        this.banco = banco;
        this.monto = monto;
        this.fecha = fecha;
    }

    public Integer getNumCheque() { return numCheque; }
    public void setNumCheque(Integer numCheque) { this.numCheque = numCheque; }

    public String getEmisor() { return emisor; }
    public void setEmisor(String emisor) { this.emisor = emisor; }

    public String getBanco() { return banco; }
    public void setBanco(String banco) { this.banco = banco; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
}
