package com.isi.desa.Model.Entities.MetodoDePago;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ChequesPropios extends MetodoDePago {
    private Integer numCheque;
    private String nombreHuesped;
    private String banco;
    private BigDecimal monto;
    private LocalDate fecha;

    public ChequesPropios() {}

    public ChequesPropios(Integer numCheque, String nombreHuesped, String banco, BigDecimal monto, LocalDate fecha) {
        this.numCheque = numCheque;
        this.nombreHuesped = nombreHuesped;
        this.banco = banco;
        this.monto = monto;
        this.fecha = fecha;
    }

    public Integer getNumCheque() { return numCheque; }
    public void setNumCheque(Integer numCheque) { this.numCheque = numCheque; }

    public String getNombreHuesped() { return nombreHuesped; }
    public void setNombreHuesped(String nombreHuesped) { this.nombreHuesped = nombreHuesped; }

    public String getBanco() { return banco; }
    public void setBanco(String banco) { this.banco = banco; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
}
