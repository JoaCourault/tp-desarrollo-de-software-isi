package com.isi.desa.Model.Entities.Habitacion;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.isi.desa.Model.Entities.Estadia.Estadia;
import com.isi.desa.Model.Entities.Reserva.Reserva;
import com.isi.desa.Model.Enums.EstadoHabitacion;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "habitacion")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_habitacion", discriminatorType = DiscriminatorType.STRING)
public abstract class Habitacion {

    @Id
    @Column(name = "id_habitacion", nullable = false)
    private String idHabitacion;

    @Column(name = "precio", precision = 38, scale = 2, nullable = false)
    private BigDecimal precio;

    @Column(name = "numero", nullable = false)
    private Integer numero;

    @Column(name = "piso", nullable = false)
    private Integer piso;

    @Column(name = "capacidad", nullable = false)
    private Integer capacidad;

    @Column(name = "detalles")
    private String detalles;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoHabitacion estado;

    // 🔥 FIX: Campo de solo lectura para leer el discriminador exacto de la BD
    @Column(name = "tipo_habitacion", insertable = false, updatable = false)
    private String tipoHabitacionString;

    // Campos específicos para camas
    @Column(name = "q_cam_individual")
    private Integer cantidadCamasIndividual;

    @Column(name = "q_cam_dobles")
    private Integer cantidadCamasDobles;

    @Column(name = "q_cam_king_size")
    private Integer cantidadCamasKingSize;

    @OneToMany(mappedBy = "habitacion")
    @JsonIgnore
    private List<Reserva> reservas = new ArrayList<>();

    @ManyToMany(mappedBy = "listaHabitaciones")
    @JsonIgnore
    private List<Estadia> estadias = new ArrayList<>();

    public Habitacion() {}

    // --- GETTERS Y SETTERS ---
    public String getIdHabitacion() { return idHabitacion; }
    public void setIdHabitacion(String idHabitacion) { this.idHabitacion = idHabitacion; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }

    public Integer getPiso() { return piso; }
    public void setPiso(Integer piso) { this.piso = piso; }

    public Integer getCapacidad() { return capacidad; }
    public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }

    public String getDetalles() { return detalles; }
    public void setDetalles(String detalles) { this.detalles = detalles; }

    public EstadoHabitacion getEstado() { return estado; }
    public void setEstado(EstadoHabitacion estado) { this.estado = estado; }

    // Getter para el tipo (lectura)
    public String getTipoHabitacionString() { return tipoHabitacionString; }

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