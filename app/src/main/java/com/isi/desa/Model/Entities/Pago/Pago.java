package com.isi.desa.Model.Entities.Pago;

import com.isi.desa.Model.Entities.MetodoDePago.MetodoDePago;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Pago {
    private BigDecimal valor;
    private LocalDateTime fecha;
    private List<MetodoDePago> metodosDePago;

    public Pago() {}
    public Pago(BigDecimal valor, LocalDateTime fecha, ArrayList<MetodoDePago> metodosDePago) {
        this.valor = valor;
        this.fecha = fecha;
        this.metodosDePago = metodosDePago;
    }

    public BigDecimal getValor() { return this.valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public LocalDateTime getFecha() { return this.fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public List<MetodoDePago> getMetodosDePago() { return this.metodosDePago; }
    public void setMetodosDePago(List<MetodoDePago> metodosDePago) { this.metodosDePago = metodosDePago; }
    public void agregarMetodoDePago(MetodoDePago metodoDePago) { this.metodosDePago.add(metodoDePago); }
}
