package com.isi.desa.Model.Entities.Habitacion;

import com.isi.desa.Model.Enums.EstadoHabitacion;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "habitacion")
public class HabitacionEntity {

    @Id
    @Column(name = "id_habitacion")
    private String idHabitacion;

    @Column(name = "precio")
    private BigDecimal precio;

    @Column(name = "numero")
    private Integer numero;

    @Column(name = "piso")
    private Integer piso;

    @Column(name = "capacidad")
    private Integer capacidad;

    @Column(name = "detalles")
    private String detalles;

    @Column(name = "tipo_habitacion")
    private String tipoHabitacion;  // 👈 importante

    @Column(name = "qcamindividual")
    private Integer cantidadCamasIndividual;

    @Column(name = "qcamdobles")
    private Integer cantidadCamasDobles;

    @Column(name = "qcamkingsize")
    private Integer cantidadCamasKingSize;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoHabitacion estado;

    // getters & setters

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

    public String getTipoHabitacion() { return tipoHabitacion; }
    public void setTipoHabitacion(String tipoHabitacion) { this.tipoHabitacion = tipoHabitacion; }

    public Integer getCantidadCamasIndividual() { return cantidadCamasIndividual; }
    public void setCantidadCamasIndividual(Integer cantidadCamasIndividual) { this.cantidadCamasIndividual = cantidadCamasIndividual; }

    public Integer getCantidadCamasDobles() { return cantidadCamasDobles; }
    public void setCantidadCamasDobles(Integer cantidadCamasDobles) { this.cantidadCamasDobles = cantidadCamasDobles; }

    public Integer getCantidadCamasKingSize() { return cantidadCamasKingSize; }
    public void setCantidadCamasKingSize(Integer cantidadCamasKingSize) { this.cantidadCamasKingSize = cantidadCamasKingSize; }

    public EstadoHabitacion getEstado() { return estado; }
    public void setEstado(EstadoHabitacion estado) { this.estado = estado; }
}
