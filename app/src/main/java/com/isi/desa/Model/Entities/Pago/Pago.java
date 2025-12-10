package com.isi.desa.Model.Entities.Pago;

import com.isi.desa.Model.Entities.Factura.Factura;
import com.isi.desa.Model.Entities.MetodoDePago.MetodoDePago;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Pago")
public class Pago {
    @Id
    @GeneratedValue(generator = "id_pago")
    @GenericGenerator(name = "id_pago", strategy = "uuid2")
    @Column(name = "id_pago", nullable = false, updatable = false)
    private String idPago;
    @Column(name = "valor")
    private BigDecimal valor;
    @Column(name = "fecha")
    private LocalDateTime fecha;

    @OneToMany
    @JoinColumn(name = "id_pago")
    private List<MetodoDePago> metodosDePago;
    @OneToOne
    @JoinColumn(name = "id_factura", unique = true)
    private Factura factura;

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
