package com.isi.desa.Model.Entities.Factura;

import java.math.BigDecimal;
import com.isi.desa.Model.Entities.Pago.Pago;

public class Factura {
    private String detalle;
    private BigDecimal total;
    private String nombre;
    private Pago pago;

    public Factura() {}

    public Factura(String detalle, BigDecimal total, String nombre, Pago pago) {
        this.detalle = detalle;
        this.total = total;
        this.nombre = nombre;
        this.pago = pago;
    }

    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Pago getPago() { return pago; }
    public void setPago(Pago pago) { this.pago = pago; }
}
