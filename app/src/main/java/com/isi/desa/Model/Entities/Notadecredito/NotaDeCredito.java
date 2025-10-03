package com.isi.desa.Model.Entities.Notadecredito;

import com.isi.desa.Model.Entities.Factura.Factura;

public class NotaDeCredito {
    private String CodigoIdentificador;
    private boolean Cobrado;
    private Factura[] Facturas;

    public NotaDeCredito() {}

    public NotaDeCredito(String CodigoIdentificador, boolean Cobrado, Factura[] Facturas) {
        this.CodigoIdentificador = CodigoIdentificador;
        this.Cobrado = Cobrado;
        this.Facturas = Facturas;
    }

    public String GetCodigoIdentificador() { return CodigoIdentificador; }
    public void SetCodigoIdentificador(String CodigoIdentificador) { this.CodigoIdentificador = CodigoIdentificador; }
    public boolean IsCobrado() { return Cobrado; }
    public void SetCobrado(boolean Cobrado) { this.Cobrado = Cobrado; }
    public Factura[] GetFacturas() { return Facturas; }
    public void SetFacturas(Factura[] Facturas) { this.Facturas = Facturas; }
}