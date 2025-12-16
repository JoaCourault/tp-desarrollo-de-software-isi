package com.isi.desa.Model.Entities.Reserva;

import com.isi.desa.Model.Entities.Habitacion.Habitacion;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.isi.desa.Model.Enums.EstadoReserva;

@Entity
@Table(name = "Reserva")
public class Reserva {
    @Id
    @GeneratedValue(generator = "id_reserva")
    @GenericGenerator(name = "id_reserva", strategy = "uuid2")
    @Column(name = "id_reserva", nullable = false, updatable = false)
    private String idReserva;
    @Column(name = "fechaIngreso")
    private LocalDateTime fechaIngreso;
    @Column(name = "fechaEgreso")
    private LocalDateTime fechaEgreso;
    @Column(name = "nombre_huesped", columnDefinition = "VARCHAR(255)")
    private String nombreHuesped;
    @Column(name = "apellido_huesped", columnDefinition = "VARCHAR(255)")
    private String apellidoHuesped;
    @Column(name = "telefono_huesped")
    private String telefonoHuesped;
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = true)
    private EstadoReserva estado;
    @ManyToOne
    @JoinColumn(name = "id_habitacion", referencedColumnName = "id_habitacion", nullable = false)
    private Habitacion habitacion;

    public Reserva() {}

    public Reserva(LocalDateTime fechaIngreso, LocalDateTime fechaEgreso) {
        this.fechaIngreso = fechaIngreso;
        this.fechaEgreso = fechaEgreso;
    }

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
    public Habitacion getHabitacion() { return habitacion; }
    public void setHabitacion(Habitacion habitacion) { this.habitacion = habitacion; }

    public String getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(String idReserva) {
        this.idReserva = idReserva;
    }

    public EstadoReserva getEstado() {return estado;  }

    public void setEstado(EstadoReserva estado) {this.estado = estado;   }
}