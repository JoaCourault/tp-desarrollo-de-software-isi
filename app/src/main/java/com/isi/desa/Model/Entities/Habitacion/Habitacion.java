package com.isi.desa.Model.Entities.Habitacion;

import com.isi.desa.Model.Entities.Estadia.Estadia;
import com.isi.desa.Model.Entities.Reserva.Reserva;
import com.isi.desa.Model.Enums.EstadoHabitacion;
import com.isi.desa.Model.Enums.TipoHabitacion;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "habitacion")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // Estrategia de tabla única
@DiscriminatorColumn(name = "tipo_habitacion", discriminatorType = DiscriminatorType.STRING) // La columna que decide la clase
public abstract class Habitacion {

    @Id
    @GeneratedValue(generator = "id_habitacion")
    @GenericGenerator(name = "id_habitacion", strategy = "uuid2")
    @Column(name = "id_habitacion", nullable = false, updatable = false)
    private String idHabitacion;

    @Column(name = "precio", nullable = false)
    private BigDecimal precio;

    @Column(name = "numero", nullable = false)
    private Integer numero;

    @Column(name = "piso", nullable = false)
    private Integer piso;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoHabitacion estado;

    @Column(name = "capacidad", nullable = false)
    private Integer capacidad;

    @Column(name = "detalles")
    private String detalles;

    // Como esta columna  es usada por el @DiscriminatorColumn de arriba,
    // marcamos como insertable=false y updatable=false para que no choquen.
    // Hibernate llenará esto automáticamente según la subclase.
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_habitacion", nullable = false, insertable = false, updatable = false)
    private TipoHabitacion tipoHabitacion;

    // Campos específicos para camas
    @Column(name = "q_cam_individual") // Sugerencia: usar snake_case en BD
    private Integer cantidadCamasIndividual;

    @Column(name = "q_cam_dobles")
    private Integer cantidadCamasDobles;

    @Column(name = "q_cam_king_size")
    private Integer cantidadCamasKingSize;

    @OneToMany(mappedBy = "habitacion")
    private List<Reserva> reservas;

    @ManyToMany(mappedBy = "habitaciones")
    private List<Estadia> estadias;

    public abstract void mostrarEstadoHabitaciones(); // Si es abstracta, fueras la implementación

    // Getters y Setters...
    // (Incluye el resto de tu código de getters y setters aquí)

    public TipoHabitacion getTipoHabitacion() { return tipoHabitacion; }

    // Este setter actualizará el objeto Java, pero NO la base de datos (porque es read-only).
    // La BD se actualiza sola al guardar la instancia de la subclase correcta.
    public void setTipoHabitacion(TipoHabitacion tipoHabitacion) { this.tipoHabitacion = tipoHabitacion; }

    // ... resto de getters y setters
    public String getIdHabitacion() { return idHabitacion; }
    public void setIdHabitacion(String idHabitacion) { this.idHabitacion = idHabitacion; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }
    public Integer getPiso() { return piso; }
    public void setPiso(Integer piso) { this.piso = piso; }
    public EstadoHabitacion getEstado() { return estado; }
    public void setEstado(EstadoHabitacion estado) { this.estado = estado; }
    public Integer getCapacidad() { return capacidad; }
    public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }
    public String getDetalles() { return detalles; }
    public void setDetalles(String detalles) { this.detalles = detalles; }
    public Integer getCantidadCamasIndividual() { return cantidadCamasIndividual; }
    public void setCantidadCamasIndividual(Integer cantidadCamasIndividual) { this.cantidadCamasIndividual = cantidadCamasIndividual; }
    public Integer getCantidadCamasDobles() { return cantidadCamasDobles; }
    public void setCantidadCamasDobles(Integer cantidadCamasDobles) { this.cantidadCamasDobles = cantidadCamasDobles; }
    public Integer getCantidadCamasKingSize() { return cantidadCamasKingSize; }
    public void setCantidadCamasKingSize(Integer cantidadCamasKingSize) { this.cantidadCamasKingSize = cantidadCamasKingSize; }
    public List<Reserva> getReservas() { return reservas; }
    public void setReservas(List<Reserva> reservas) { this.reservas = reservas; }
    public List<Estadia> getEstadias() { return estadias; }
    public void setEstadias(List<Estadia> estadias) { this.estadias = estadias; }
}