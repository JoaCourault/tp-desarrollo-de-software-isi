package com.isi.desa.Model.Entities.Habitacion;

import com.isi.desa.Model.Entities.Reserva.Reserva;
import com.isi.desa.Model.Enums.EstadoHabitacion;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "Habitacion")
public abstract class Habitacion {
    @Id
    @GeneratedValue(generator = "id_habitacion")
    @GenericGenerator(name = "id_habitacion", strategy = "uuid2")
    private String idHabitacion;
    @Column(name = "precio")
    private BigDecimal precio;
    @Column(name = "numero")
    private Integer numero;
    @Column(name = "piso")
    private Integer piso;
    @Column(name = "estado")
    private EstadoHabitacion estado;
    @Column(name = "capacidad")
    private Integer capacidad;
    @Column(name = "detalles")
    private String detalles;
    @OneToMany(mappedBy = "habitacion")
    private List<Reserva> reservas;

    public abstract void mostrarEstadoHabitaciones();

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
    public List<Reserva> getReservas() { return reservas; }
    public void setReservas(List<Reserva> reservas) { this.reservas = reservas; }
}