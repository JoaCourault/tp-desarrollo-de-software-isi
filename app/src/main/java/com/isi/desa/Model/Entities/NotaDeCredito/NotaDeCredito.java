package com.isi.desa.Model.Entities.NotaDeCredito;

import com.isi.desa.Model.Entities.Factura.Factura;

public class NotaDeCredito {
    private String codigoIdentificador;
    private boolean cobrado;
    private Factura[] facturas;

    public NotaDeCredito() {}

    public NotaDeCredito(String codigoIdentificador, boolean cobrado, Factura[] facturas) {
        this.codigoIdentificador = codigoIdentificador;
        this.cobrado = cobrado;
        this.facturas = facturas;
    }

    public String getCodigoIdentificador() { return codigoIdentificador; }
    public void setCodigoIdentificador(String codigoIdentificador) { this.codigoIdentificador = codigoIdentificador; }
    public boolean isCobrado() { return cobrado; }
    public void setCobrado(boolean cobrado) { this.cobrado = cobrado; }
    public Factura[] getFacturas() { return facturas; }
    public void setFacturas(Factura[] facturas) { this.facturas = facturas; }
}