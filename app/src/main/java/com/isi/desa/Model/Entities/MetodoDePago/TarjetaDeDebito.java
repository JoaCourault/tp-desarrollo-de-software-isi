package com.isi.desa.Model.Entities.MetodoDePago;

import java.time.LocalDate;
import com.isi.desa.Model.Enums.EmisorTarjeta;

public class TarjetaDeDebito extends MetodoDePago {
    private EmisorTarjeta emisor;
    private String titular;
    private Integer dniTitular;
    private Integer numeroTarj;
    private LocalDate caducidad;
    private Integer codigoCVV;
    private String correo;

    public TarjetaDeDebito() {}

    public TarjetaDeDebito(EmisorTarjeta emisor, String titular, Integer dniTitular, Integer numeroTarj, LocalDate caducidad, Integer codigoCVV, String correo) {
        this.emisor = emisor;
        this.titular = titular;
        this.dniTitular = dniTitular;
        this.numeroTarj = numeroTarj;
        this.caducidad = caducidad;
        this.codigoCVV = codigoCVV;
        this.correo = correo;
    }

    public EmisorTarjeta getEmisor() { return emisor; }
    public void setEmisor(EmisorTarjeta emisor) { this.emisor = emisor; }

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
}
