package com.isi.desa.Model.Entities.Servicio;

import com.isi.desa.Model.Entities.Estadia.Estadia;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
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

    public abstract void cargarServicio();
    public abstract BigDecimal calcularPrecio();

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public Estadia getEstadia() { return estadia; }
    public void setEstadia(Estadia estadia) { this.estadia = estadia; }
}
