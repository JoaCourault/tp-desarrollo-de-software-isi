package com.isi.desa.Dto.Factura;

import com.isi.desa.Dto.Resultado;

public class ConfirmarFacturacionResult {
    public Resultado resultado;
    public FacturaDTO facturaConfirmada;

    public ConfirmarFacturacionResult() {
        resultado = new Resultado();
    }
}
