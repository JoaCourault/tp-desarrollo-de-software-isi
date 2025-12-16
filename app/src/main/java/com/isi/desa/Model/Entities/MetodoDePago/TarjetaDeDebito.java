package com.isi.desa.Model.Entities.MetodoDePago;

import java.time.LocalDate;
import com.isi.desa.Model.Enums.EmisorTarjeta;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "TarjetaDeDebito")
public class TarjetaDeDebito extends MetodoDePago {
    @Column(name = "emisor")
    private EmisorTarjeta emisor;
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

    public TarjetaDeDebito() {}

    public TarjetaDeDebito(EmisorTarjeta emisor, String titular, String dniTitular, String numeroTarj, LocalDate caducidad, String codigoCVV, String correo) {
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

    public String getIdMetodoDePago() { return super.getIdMetodoDePago(); }
    public void setIdMetodoDePago(String idMetodoDePago) { super.setIdMetodoDePago(idMetodoDePago); }
}