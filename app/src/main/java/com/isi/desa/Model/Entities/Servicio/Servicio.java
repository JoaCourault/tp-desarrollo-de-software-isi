package com.isi.desa.Model.Entities.Servicio;

import com.isi.desa.Model.Entities.Estadia.Estadia;
import com.isi.desa.Model.Entities.Factura.Factura;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Servicio")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Servicio {
    @Id
    @GeneratedValue(generator = "id_servicio")
    @GenericGenerator(name = "id_servicio", strategy = "uuid2")
    @Column(name = "id_servicio", updatable = false, nullable = false)
    protected String id;
    @Column(name = "fecha")
    protected LocalDateTime fecha;
    @Column(name = "precio")
    protected BigDecimal precio;
    @ManyToOne
    @JoinColumn(name = "id_estadia", referencedColumnName = "id_estadia")
    protected Estadia estadia;

    @ManyToMany(mappedBy = "servicios")
    protected List<Factura> facturas;

    public abstract void cargarServicio();
    public abstract BigDecimal calcularPrecio();

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public Estadia getEstadia() { return estadia; }
    public void setEstadia(Estadia estadia) { this.estadia = estadia; }

    public  List<Factura> getFacturas() { return facturas; }
    public void setFacturas(List<Factura> facturas) { this.facturas = facturas; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}