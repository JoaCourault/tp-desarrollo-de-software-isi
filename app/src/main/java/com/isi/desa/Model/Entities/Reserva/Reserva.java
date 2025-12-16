package com.isi.desa.Model.Entities.Reserva;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.isi.desa.Model.Entities.Habitacion.Habitacion;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reserva")
public class Reserva {

    @Id
    @GeneratedValue(generator = "id_reserva")
    @GenericGenerator(name = "id_reserva", strategy = "uuid2")
    @Column(name = "id_reserva", nullable = false, updatable = false)
    private String idReserva;

    // ✅ Coincide con BD: fecha_ingreso TIMESTAMP
    @Column(name = "fecha_ingreso")
    private LocalDateTime fechaIngreso;

    // ✅ Coincide con BD: fecha_egreso TIMESTAMP
    @Column(name = "fecha_egreso")
    private LocalDateTime fechaEgreso;

    @Column(name = "nombre_huesped", columnDefinition = "VARCHAR(255)")
    private String nombreHuesped;

    @Column(name = "apellido_huesped", columnDefinition = "VARCHAR(255)")
    private String apellidoHuesped;

    @Column(name = "telefono_huesped")
    private String telefonoHuesped;

    // ✅ Coincide con BD: estado VARCHAR(50) + CHECK
    @Column(name = "estado", length = 50, nullable = false)
    private String estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_habitacion", referencedColumnName = "id_habitacion", nullable = false)
    @JsonIgnore
    private Habitacion habitacion;

    public Reserva() {}

    public Reserva(LocalDateTime fechaIngreso, LocalDateTime fechaEgreso) {
        this.fechaIngreso = fechaIngreso;
        this.fechaEgreso = fechaEgreso;
    }

    // ---------------- GETTERS / SETTERS ----------------

    public String getIdReserva() { return idReserva; }
    public void setIdReserva(String idReserva) { this.idReserva = idReserva; }

    public LocalDateTime getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDateTime fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public LocalDateTime getFechaEgreso() { return fechaEgreso; }
    public void setFechaEgreso(LocalDateTime fechaEgreso) { this.fechaEgreso = fechaEgreso; }

    public String getNombreHuesped() { return nombreHuesped; }
    public void setNombreHuesped(String nombreHuesped) { this.nombreHuesped = nombreHuesped; }

    public String getApellidoHuesped() { return apellidoHuesped; }
    public void setApellidoHuesped(String apellidoHuesped) { this.apellidoHuesped = apellidoHuesped; }

    public String getTelefonoHuesped() { return telefonoHuesped; }
    public void setTelefonoHuesped(String telefonoHuesped) { this.telefonoHuesped = telefonoHuesped; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Habitacion getHabitacion() { return habitacion; }
    public void setHabitacion(Habitacion habitacion) { this.habitacion = habitacion; }

    // ---------------------------------------------------------
    // ✅ Compatibilidad: tu código viejo usa LocalDate fechaDesde/fechaHasta
    // No existen en BD, se derivan de TIMESTAMP
    // ---------------------------------------------------------

    @Transient
    public LocalDate getFechaDesde() {
        return (fechaIngreso != null) ? fechaIngreso.toLocalDate() : null;
    }

    public void setFechaDesde(LocalDate fechaDesde) {
        if (fechaDesde == null) {
            this.fechaIngreso = null;
        } else {
            this.fechaIngreso = fechaDesde.atTime(14, 0); // check-in
        }
    }

    @Transient
    public LocalDate getFechaHasta() {
        return (fechaEgreso != null) ? fechaEgreso.toLocalDate() : null;
    }

    public void setFechaHasta(LocalDate fechaHasta) {
        if (fechaHasta == null) {
            this.fechaEgreso = null;
        } else {
            this.fechaEgreso = fechaHasta.atTime(10, 0); // check-out
        }
    }
}
