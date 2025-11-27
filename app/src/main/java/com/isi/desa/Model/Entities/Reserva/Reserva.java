package com.isi.desa.Model.Entities.Reserva;

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

    // ---- FK ÚNICA obligatoria ----
    @ManyToOne
    @JoinColumn(name = "id_habitacion", referencedColumnName = "id_habitacion", nullable = false)
    private HabitacionEntity habitacion;

    @Column(name = "fecha_desde", nullable = false)
    private LocalDate fechaDesde;

    @Column(name = "fecha_hasta", nullable = false)
    private LocalDate fechaHasta;

    @Column(name = "fecha_ingreso")
    private LocalDate fechaIngreso;

    @Column(name = "fecha_egreso")
    private LocalDate fechaEgreso;

    @Column(name = "estado", nullable = false)
    private String estado;

    // ============================
    // NUEVOS CAMPOS AGREGADOS
    // ============================
    @Column(name = "nombre_responsable")
    private String nombreResponsable;

    @Column(name = "apellido_responsable")
    private String apellidoResponsable;

    @Column(name = "telefono_responsable")
    private String telefonoResponsable;

    // ---- Constructores ----

    public Reserva() {
        this.idReserva = UUID.randomUUID().toString();
    }

    public Reserva(HabitacionEntity habitacion,
                   LocalDate fechaDesde,
                   LocalDate fechaHasta,
                   String nombre,
                   String apellido,
                   String telefono) {

        this.idReserva = UUID.randomUUID().toString();
        this.habitacion = habitacion;
        this.fechaDesde = fechaDesde;
        this.fechaHasta = fechaHasta;

        this.fechaIngreso = fechaDesde;
        this.fechaEgreso = fechaHasta;

        this.nombreResponsable = nombre;
        this.apellidoResponsable = apellido;
        this.telefonoResponsable = telefono;

        this.estado = "RESERVADA";
    }

    // ---- Getters y Setters ----

    public String getIdReserva() { return idReserva; }
    public void setIdReserva(String idReserva) { this.idReserva = idReserva; }

    public HabitacionEntity getHabitacion() { return habitacion; }
    public void setHabitacion(HabitacionEntity habitacion) { this.habitacion = habitacion; }

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

    public String getNombreResponsable() { return nombreResponsable; }
    public void setNombreResponsable(String nombreResponsable) { this.nombreResponsable = nombreResponsable; }

    public String getApellidoResponsable() { return apellidoResponsable; }
    public void setApellidoResponsable(String apellidoResponsable) { this.apellidoResponsable = apellidoResponsable; }

    public String getTelefonoResponsable() { return telefonoResponsable; }
    public void setTelefonoResponsable(String telefonoResponsable) { this.telefonoResponsable = telefonoResponsable; }
}
