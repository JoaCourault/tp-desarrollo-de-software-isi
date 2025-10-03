package com.isi.desa.Model.Entities.MetodoDePago;

import java.util.Date;

public class ChequesPropios extends MetodoDePago {
    private Integer numCheque;
    private String nombreHuesped;
    private String banco;
    private Float monto;
    private Date fecha;

    public ChequesPropios() {}

    public ChequesPropios(Integer numCheque, String nombreHuesped, String banco, Float monto, Date fecha) {
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

    public Float getMonto() { return monto; }
    public void setMonto(Float monto) { this.monto = monto; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
}
