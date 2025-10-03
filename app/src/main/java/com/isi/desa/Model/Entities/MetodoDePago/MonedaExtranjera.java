package com.isi.desa.Model.Entities.MetodoDePago;

import com.isi.desa.Model.Enums.TipoMoneda;

public class MonedaExtranjera extends MetodoDePago {
    private TipoMoneda moneda;

    public MonedaExtranjera() {}

    public MonedaExtranjera(TipoMoneda moneda) {
        this.moneda = moneda;
    }

    public TipoMoneda getMoneda() { return moneda; }
    public void setMoneda(TipoMoneda moneda) { this.moneda = moneda; }
}
