package com.isi.desa.Model.Entities.Reserva;

import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Model.Entities.Habitacion.HabitacionEntity;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "reserva")
public class Reserva {

    @Id
    @Column(name = "id_reserva", nullable = false)
    private String idReserva;

    // Relación opcional (puede ser null)
    @ManyToOne
    @JoinColumn(name = "id_huesped", referencedColumnName = "id_huesped", nullable = true)
    private Huesped huesped;

    @ManyToOne
    @JoinColumn(name = "id_habitacion", referencedColumnName = "id_habitacion")
    private HabitacionEntity habitacion;

    // --- NUEVOS CAMPOS DE CONTACTO (Datos crudos) ---
    @Column(name = "nombre_cliente")
    private String nombreCliente;

    @Column(name = "apellido_cliente")
    private String apellidoCliente;

    @Column(name = "telefono_cliente")
    private String telefonoCliente;

    @Column(name = "email_cliente")
    private String emailCliente;

    // Fechas y Estado
    @Column(name = "fecha_desde") private LocalDate fechaDesde;
    @Column(name = "fecha_hasta") private LocalDate fechaHasta;
    @Column(name = "fecha_ingreso") private LocalDate fechaIngreso;
    @Column(name = "fecha_egreso") private LocalDate fechaEgreso;
    @Column(name = "estado") private String estado;

    public Reserva() {
        this.idReserva = "RES-" + UUID.randomUUID().toString().substring(0, 8);
    }

    // --- GETTERS Y SETTERS ---
    public String getIdReserva() { return idReserva; }
    public void setIdReserva(String idReserva) { this.idReserva = idReserva; }

    public Huesped getHuesped() { return huesped; }
    public void setHuesped(Huesped huesped) { this.huesped = huesped; }

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