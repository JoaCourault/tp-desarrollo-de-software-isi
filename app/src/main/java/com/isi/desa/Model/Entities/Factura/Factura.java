package com.isi.desa.Model.Entities.Factura;

import java.math.BigDecimal;
import java.time.LocalDateTime; // <--- IMPORTANTE: Importar LocalDateTime
import java.util.List;

import com.isi.desa.Model.Entities.Estadia.Estadia;
import com.isi.desa.Model.Entities.NotaDeCredito.NotaDeCredito;
import com.isi.desa.Model.Entities.Pago.Pago;
import com.isi.desa.Model.Entities.ResponsableDePago.ResponsableDePago;
import com.isi.desa.Model.Entities.Servicio.Servicio;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "Factura")
public class Factura {
    @Id
    @GeneratedValue(generator = "id_factura")
    @GenericGenerator(name = "id_factura", strategy = "uuid2")
    @Column(name = "id_factura", nullable = false, updatable = false)
    private String idFactura;

    @Column(name = "detalle")
    private String detalle;

    @Column(name = "total")
    private BigDecimal total;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    @Column(name = "tipo_factura") // "A", "B", "C"
    private String tipo;

    @ManyToOne
    @JoinColumn(name = "id_responsable")
    private ResponsableDePago responsableDePago;

    @OneToOne(mappedBy = "factura", optional = true)
    private Pago pago;

    @ManyToOne(optional = true)
    @JoinColumn(name = "cod_identificador_nota_credito")
    private NotaDeCredito notaDeCredito;

    @ManyToMany
    @JoinTable(
            name = "FacturacionEstadia",
            joinColumns = @JoinColumn(name = "id_factura"),
            inverseJoinColumns = @JoinColumn(name = "id_estadia")
    )
    private List<Estadia> estadias;

    @ManyToMany
    @JoinTable(
            name = "FacturacionServicio",
            joinColumns = @JoinColumn(name = "id_factura"),
            inverseJoinColumns = @JoinColumn(name = "id_servicio")
    )
    private List<Servicio> servicios;

    public Factura() {}

    public Factura(String detalle, BigDecimal total, String nombre, Pago pago) {
        this.detalle = detalle;
        this.total = total;
        this.nombre = nombre;
        this.pago = pago;
    }

    // --- GETTERS Y SETTERS ---

    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public ResponsableDePago getResponsableDePago() { return responsableDePago; }
    public void setResponsableDePago(ResponsableDePago responsableDePago) { this.responsableDePago = responsableDePago; }

    public Pago getPago() { return pago; }
    public void setPago(Pago pago) { this.pago = pago; }

    public NotaDeCredito getNotaDeCredito() { return notaDeCredito; }
    public void setNotaDeCredito(NotaDeCredito notaDeCredito) { this.notaDeCredito = notaDeCredito; }

    public List<Servicio> getServicios() { return servicios; }
    public void setServicios(List<Servicio> servicios) { this.servicios = servicios; }

    public List<Estadia> getEstadias() { return estadias; }
    public void setEstadias(List<Estadia> estadias) { this.estadias = estadias; }

    public String getIdFactura() { return idFactura; }
    public void setIdFactura(String idFactura) { this.idFactura = idFactura; }
}