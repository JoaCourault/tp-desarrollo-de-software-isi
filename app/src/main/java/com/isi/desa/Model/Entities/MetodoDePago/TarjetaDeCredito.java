package com.isi.desa.Model.Entities.MetodoDePago;

import java.time.LocalDate;

public class TarjetaDeCredito extends MetodoDePago {
    private String titular;
    private Integer dniTitular;
    private Integer numeroTarj;
    private LocalDate caducidad;
    private Integer codigoCVV;
    private String correo;
    private Integer cuotas;

    public TarjetaDeCredito() {}

    public TarjetaDeCredito(String titular, Integer dniTitular, Integer numeroTarj, LocalDate caducidad, Integer codigoCVV, String correo, Integer cuotas) {
        this.titular = titular;
        this.dniTitular = dniTitular;
        this.numeroTarj = numeroTarj;
        this.caducidad = caducidad;
        this.codigoCVV = codigoCVV;
        this.correo = correo;
        this.cuotas = cuotas;
    }
    public String getTitular() { return titular; }
    public void setTitular(String titular) { this.titular = titular; }

    public Integer getDniTitular() { return dniTitular; }
    public void setDniTitular(Integer dniTitular) { this.dniTitular = dniTitular; }

    public Integer getNumeroTarj() { return numeroTarj; }
    public void setNumeroTarj(Integer numeroTarj) { this.numeroTarj = numeroTarj; }

    public LocalDate getCaducidad() { return caducidad; }
    public void setCaducidad(LocalDate caducidad) { this.caducidad = caducidad; }

    public Integer getCodigoCVV() { return codigoCVV; }
    public void setCodigoCVV(Integer codigoCVV) { this.codigoCVV = codigoCVV; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public Integer getCuotas() { return cuotas; }
    public void setCuotas(Integer cuotas) { this.cuotas = cuotas; }
}
