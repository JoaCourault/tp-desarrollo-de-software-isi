package com.isi.desa.Model.Entities.Reserva;

import com.isi.desa.Model.Entities.Estadia.Estadia;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Model.Entities.Habitacion.HabitacionEntity;
import com.fasterxml.jackson.annotation.JsonIgnore; // Importante
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "reserva")
public class Reserva {

    @Id
    @Column(name = "id_reserva", nullable = false)
    private String idReserva;

    // --- CORRECCIÓN DEL ERROR ---
    // NO uses @JoinColumn aquí.
    // Usa mappedBy apuntando al nombre de la variable en la clase Estadia ("reserva").
    @OneToOne(mappedBy = "reserva", cascade = CascadeType.ALL)
    @JsonIgnore // Evita bucle infinito si conviertes a JSON
    private Estadia estadia;

    @ManyToOne
    @JoinColumn(name = "id_habitacion", referencedColumnName = "id_habitacion")
    private HabitacionEntity habitacion;

    // ... resto de atributos y getters/setters ...
    @Column(name = "nombre_cliente") private String nombreCliente;
    @Column(name = "apellido_cliente") private String apellidoCliente;
    @Column(name = "telefono_cliente") private String telefonoCliente;
    @Column(name = "fecha_desde") private LocalDate fechaDesde;
    @Column(name = "fecha_hasta") private LocalDate fechaHasta;
    @Column(name = "fecha_ingreso") private LocalDate fechaIngreso;
    @Column(name = "fecha_egreso") private LocalDate fechaEgreso;
    @Column(name = "estado") private String estado;

    public Reserva() {
    }

    // Getters y Setters
    public String getIdReserva() { return idReserva; }
    public void setIdReserva(String idReserva) { this.idReserva = idReserva; }

    public Estadia getEstadia() { return estadia; }
    public void setEstadia(Estadia estadia) { this.estadia = estadia; }

    public HabitacionEntity getHabitacion() { return habitacion; }
    public void setHabitacion(HabitacionEntity habitacion) { this.habitacion = habitacion; }
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
    public String getApellidoCliente() { return apellidoCliente; }
    public void setApellidoCliente(String apellidoCliente) { this.apellidoCliente = apellidoCliente; }
    public String getTelefonoCliente() { return telefonoCliente; }
    public void setTelefonoCliente(String telefonoCliente) { this.telefonoCliente = telefonoCliente; }
    public LocalDate getFechaDesde() { return fechaDesde; }
    public void setFechaDesde(LocalDate fechaDesde) { this.fechaDesde = fechaDesde; }
    public LocalDate getFechaHasta() { return fechaHasta; }
    public void setFechaHasta(LocalDate fechaHasta) { this.fechaHasta = fechaHasta; }
    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }
    public LocalDate getFechaEgreso() { return fechaEgreso; }
    public void setFechaEgreso(LocalDate fechaEgreso) { this.fechaEgreso = fechaEgreso; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}