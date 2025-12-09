package com.isi.desa.Dto.Habitacion;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.isi.desa.Model.Enums.EstadoHabitacion;

public class HabitacionDTO {

    // Vincula "idHabitacion" de Java con "id_habitacion" del JSON/Front
    @JsonProperty("id_habitacion")
    private String idHabitacion;

    private Float precio;
    private Integer numero;
    private Integer piso;
    private EstadoHabitacion estado;
    private Integer capacidad;
    private String detalles;

    // --- NUEVO CAMPO NECESARIO PARA LA GRILLA ---
    private String tipoHabitacion;

    private Integer cantidadCamasIndividual;
    private Integer cantidadCamasDobles;
    private Integer cantidadCamasKingSize;

    public HabitacionDTO() {}

    // --- GETTERS Y SETTERS ---

    public String getIdHabitacion() { return idHabitacion; }
    public void setIdHabitacion(String idHabitacion) { this.idHabitacion = idHabitacion; }

    public Float getPrecio() { return precio; }
    public void setPrecio(Float precio) { this.precio = precio; }

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

    public String getTipoHabitacion() { return tipoHabitacion; }
    public void setTipoHabitacion(String tipoHabitacion) { this.tipoHabitacion = tipoHabitacion; }

    public Integer getCantidadCamasIndividual() { return cantidadCamasIndividual; }
    public void setCantidadCamasIndividual(Integer cantidadCamasIndividual) { this.cantidadCamasIndividual = cantidadCamasIndividual; }

    public Integer getCantidadCamasDobles() { return cantidadCamasDobles; }
    public void setCantidadCamasDobles(Integer cantidadCamasDobles) { this.cantidadCamasDobles = cantidadCamasDobles; }

    public Integer getCantidadCamasKingSize() { return cantidadCamasKingSize; }
    public void setCantidadCamasKingSize(Integer cantidadCamasKingSize) { this.cantidadCamasKingSize = cantidadCamasKingSize; }
}