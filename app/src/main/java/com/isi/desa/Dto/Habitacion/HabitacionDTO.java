package com.isi.desa.Dto.Habitacion;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.isi.desa.Model.Enums.EstadoHabitacion;
import com.isi.desa.Model.Enums.TipoHabitacion;

import java.math.BigDecimal;

public class HabitacionDTO {
    @JsonProperty("id_habitacion")
    public String idHabitacion;
    public BigDecimal precio;
    public Integer numero;
    public Integer piso;
    public EstadoHabitacion estado;
    public Integer capacidad;
    public String detalles;
    public TipoHabitacion tipoHabitacion;

    public Integer cantidadCamasDobles;
    public Integer cantidadCamasKingSize;

    public String getIdHabitacion() {
        return idHabitacion;
    }

    public void setIdHabitacion(String idHabitacion) {
        this.idHabitacion = idHabitacion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Integer getPiso() {
        return piso;
    }

    public void setPiso(Integer piso) {
        this.piso = piso;
    }

    public EstadoHabitacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoHabitacion estado) {
        this.estado = estado;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }

    public TipoHabitacion getTipoHabitacion() {
        return tipoHabitacion;
    }

    public void setTipoHabitacion(TipoHabitacion tipoHabitacion) {
        this.tipoHabitacion = tipoHabitacion;
    }

    public Integer getCantidadCamasDobles() {
        return cantidadCamasDobles;
    }

    public void setCantidadCamasDobles(Integer cantidadCamasDobles) {
        this.cantidadCamasDobles = cantidadCamasDobles;
    }

    public Integer getCantidadCamasKingSize() {
        return cantidadCamasKingSize;
    }

    public void setCantidadCamasKingSize(Integer cantidadCamasKingSize) {
        this.cantidadCamasKingSize = cantidadCamasKingSize;
    }
}
