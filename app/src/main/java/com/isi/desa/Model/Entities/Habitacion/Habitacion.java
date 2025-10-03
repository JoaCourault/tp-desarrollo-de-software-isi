package com.isi.desa.Model.Entities.Habitacion;

import com.isi.desa.Model.Enums.EstadoHabitacion;

import java.math.BigDecimal;

public abstract class Habitacion {
    private String idHabitacion;
    private BigDecimal precio;
    private Integer numero;
    private Integer piso;
    private EstadoHabitacion estado;
    private Integer capacidad;
    private String detalles;

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
}