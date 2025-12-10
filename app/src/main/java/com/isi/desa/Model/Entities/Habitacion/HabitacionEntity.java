package com.isi.desa.Model.Entities.Habitacion;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.isi.desa.Model.Entities.Estadia.Estadia;
import com.isi.desa.Model.Enums.EstadoHabitacion;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "habitacion")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // Todos los hijos en la misma tabla
@DiscriminatorColumn(name = "tipo_habitacion", discriminatorType = DiscriminatorType.STRING) // Columna que decide la clase
public abstract class HabitacionEntity { // <--- CLASE ABSTRACTA

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

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoHabitacion estado;

    @ManyToMany(mappedBy = "listaHabitaciones")
    @JsonIgnore
    private List<Estadia> estadias = new ArrayList<>();

    public HabitacionEntity() {}

    // --- GETTERS Y SETTERS COMUNES ---
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

    public EstadoHabitacion getEstado() { return estado; }
    public void setEstado(EstadoHabitacion estado) { this.estado = estado; }

    public List<Estadia> getEstadias() { return estadias; }
    public void setEstadias(List<Estadia> estadias) { this.estadias = estadias; }

    // Método abstracto opcional para obligar a los hijos a definirse
    // public abstract String getTipoDescripcion();
}