package com.isi.desa.Model.Entities.Habitacion;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.isi.desa.Model.Enums.EstadoHabitacion;
import com.isi.desa.Model.Entities.Estadia.Estadia; // Asegúrate de importar esto
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "habitacion")
public class HabitacionEntity {

    @Id
    @Column(name = "id_habitacion")
    private String idHabitacion;

    @Column(name = "precio")
    private Float precio;

    @Column(name = "numero")
    private Integer numero;

    @Column(name = "piso")
    private Integer piso;

    @Column(name = "capacidad")
    private Integer capacidad;

    @Column(name = "detalles")
    private String detalles;

    @Column(name = "tipo_habitacion")
    private String tipoHabitacion;

    @Column(name = "qcamindividual")
    private Integer cantidadCamasIndividual;

    @Column(name = "qcamdobles")
    private Integer cantidadCamasDobles;

    @Column(name = "qcamkingsize")
    private Integer cantidadCamasKingSize;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoHabitacion estado;

    // --- CORRECCIÓN DEL ERROR ---
    // ANTES: @Column(name = "Lista_estadia") -> ERROR: Hibernate intenta guardar la lista como bytes.
    // AHORA: @ManyToMany(mappedBy = "listaHabitaciones") -> CORRECTO: Es la inversa de Estadia.
    @ManyToMany(mappedBy = "listaHabitaciones")
    @JsonIgnore
    private List<Estadia> estadias = new ArrayList<>();

    // Constructor vacío
    public HabitacionEntity() {}

    // --- GETTERS Y SETTERS ---
    public String getIdHabitacion() { return idHabitacion; }
    public void setIdHabitacion(String idHabitacion) { this.idHabitacion = idHabitacion; }

    public Float getPrecio() { return precio; }
    public void setPrecio(Float precio) { this.precio = precio; }

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

    // Getter y Setter para la lista corregida
    public List<Estadia> getEstadias() { return estadias; }
    public void setEstadias(List<Estadia> estadias) { this.estadias = estadias; }
}