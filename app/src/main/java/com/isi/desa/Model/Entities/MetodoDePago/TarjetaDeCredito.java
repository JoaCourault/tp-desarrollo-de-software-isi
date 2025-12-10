package com.isi.desa.Model.Entities.MetodoDePago;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "TarjetaDeCredito")
public class TarjetaDeCredito extends MetodoDePago {
    @Column(name = "titular")
    private String titular;
    @Column(name = "dni_titular")
    private String dniTitular;
    @Column(name = "numero_tarj")
    private String numeroTarj;
    @Column(name = "caducidad")
    private LocalDate caducidad;
    @Column(name = "codigo_CVV")
    private String codigoCVV;
    @Column(name = "correo")
    private String correo;
    @Column(name = "cuotas")
    private Integer cuotas;

    public TarjetaDeCredito() {}

    public TarjetaDeCredito(String titular, String dniTitular, String numeroTarj, LocalDate caducidad, String codigoCVV, String correo, Integer cuotas) {
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

    public String getDniTitular() { return dniTitular; }
    public void setDniTitular(String dniTitular) { this.dniTitular = dniTitular; }

    public String getNumeroTarj() { return numeroTarj; }
    public void setNumeroTarj(String numeroTarj) { this.numeroTarj = numeroTarj; }

    public LocalDate getCaducidad() { return caducidad; }
    public void setCaducidad(LocalDate caducidad) { this.caducidad = caducidad; }

    public String getCodigoCVV() { return codigoCVV; }
    public void setCodigoCVV(String codigoCVV) { this.codigoCVV = codigoCVV; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public Integer getCuotas() { return cuotas; }
    public void setCuotas(Integer cuotas) { this.cuotas = cuotas; }
}
