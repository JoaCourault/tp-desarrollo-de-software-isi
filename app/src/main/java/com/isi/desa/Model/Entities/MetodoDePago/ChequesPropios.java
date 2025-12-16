package com.isi.desa.Model.Entities.MetodoDePago;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "ChequesPropios")
public class ChequesPropios extends MetodoDePago {
    @Column(name = "num_cheque")
    private Integer numCheque;
    @Column(name = "nombre_huesped")
    private String nombreHuesped;
    @Column(name = "banco")
    private String banco;
    @Column(name = "monto")
    private BigDecimal monto;
    @Column(name = "fecha")
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

    public String getIdMetodoDePago() { return super.getIdMetodoDePago(); }
    public void setIdMetodoDePago(String idMetodoDePago) { super.setIdMetodoDePago(idMetodoDePago); }
}