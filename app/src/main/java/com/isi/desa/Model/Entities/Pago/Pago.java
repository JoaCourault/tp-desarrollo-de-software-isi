package com.isi.desa.Model.Entities.Pago;

import com.isi.desa.Model.Entities.MetodoDePago.MetodoDePago;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Pago {
    private BigDecimal Valor;
    private Date Fecha;
    private List<MetodoDePago> MetodosDePago;

    public Pago() {}
    public Pago(BigDecimal Valor, Date Fecha, ArrayList<MetodoDePago> MetodosDePago) {
        this.Valor = Valor;
        this.Fecha = Fecha;
        this.MetodosDePago = MetodosDePago;
    }

    public BigDecimal GetValor() { return this.Valor; }
    public void SetValor(BigDecimal Valor) { this.Valor = Valor; }

    public Date GetFecha() { return this.Fecha; }
    public void SetFecha(Date Fecha) { this.Fecha = Fecha; }

    public List<MetodoDePago> GetMetodosDePago() { return this.MetodosDePago; }
    public void SetMetodosDePago(List<MetodoDePago> MetodosDePago) { this.MetodosDePago = MetodosDePago; }
    public void AgregarMetodoDePago(MetodoDePago MetodoDePago) { this.MetodosDePago.add(MetodoDePago); }
}
