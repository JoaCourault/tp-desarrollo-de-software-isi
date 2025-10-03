package com.isi.desa.Model.Entities.MetodoDePago;

import java.util.Date;

public class ChequesDeTerceros extends MetodoDePago {
    private Integer numCheque;
    private String emisor;
    private String banco;
    private Float monto;
    private Date fecha;

    public ChequesDeTerceros() {}

    public ChequesDeTerceros(Integer numCheque, String emisor, String banco, Float monto, Date fecha) {
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

    public Float getMonto() { return monto; }
    public void setMonto(Float monto) { this.monto = monto; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
}
