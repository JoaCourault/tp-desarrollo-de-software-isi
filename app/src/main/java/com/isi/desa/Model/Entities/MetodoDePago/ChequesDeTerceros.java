package com.isi.desa.Model.Entities.MetodoDePago;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "ChequesDeTerceros")
public class ChequesDeTerceros extends MetodoDePago {
    @Column(name = "num_cheque")
    private Integer numCheque;
    @Column(name = "emisor")
    private String emisor;
    @Column(name = "banco")
    private String banco;
    @Column(name = "fecha")
    private LocalDate fecha;

    public ChequesDeTerceros() {}

    public ChequesDeTerceros(Integer numCheque, String emisor, String banco, LocalDate fecha) {
        this.numCheque = numCheque;
        this.emisor = emisor;
        this.banco = banco;
        this.fecha = fecha;
    }

    public Integer getNumCheque() { return numCheque; }
    public void setNumCheque(Integer numCheque) { this.numCheque = numCheque; }

    public String getEmisor() { return emisor; }
    public void setEmisor(String emisor) { this.emisor = emisor; }

    public String getBanco() { return banco; }
    public void setBanco(String banco) { this.banco = banco; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getIdMetodoDePago() { return super.getIdMetodoDePago(); }
    public void setIdMetodoDePago(String idMetodoDePago) { super.setIdMetodoDePago(idMetodoDePago); }
}