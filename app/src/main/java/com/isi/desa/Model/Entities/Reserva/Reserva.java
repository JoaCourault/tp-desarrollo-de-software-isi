package com.isi.desa.Model.Entities.Reserva;

import com.isi.desa.Model.Entities.Habitacion.Habitacion;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDate;

@Entity
@Table(name = "Reserva")
public class Reserva {
    @Id
    @GeneratedValue(generator = "id_reserva")
    @GenericGenerator(name = "id_reserva", strategy = "uuid2")
    @Column(name = "id_reserva", nullable = false, updatable = false)
    private String idReserva;
    @Column(name = "fechaIngreso")
    private LocalDate fechaIngreso;
    @Column(name = "fechaEgreso")
    private LocalDate fechaEgreso;
    @Column(name = "nombre_huesped")
    private String nombreHuesped;
    @Column(name = "apellido_huesped")
    private String apellidoHuesped;
    @Column(name = "telefono_huesped")
    private String telefonoHuesped;
    @ManyToOne
    @JoinColumn(name = "id_habitacion", referencedColumnName = "id_habitacion", nullable = false)
    private Habitacion habitacion;

    public Reserva() {}

    public Reserva(LocalDate fechaIngreso, LocalDate fechaEgreso) {
        this.fechaIngreso = fechaIngreso;
        this.fechaEgreso = fechaEgreso;
    }

    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }
    public LocalDate getFechaEgreso() { return fechaEgreso; }
    public void setFechaEgreso(LocalDate fechaEgreso) { this.fechaEgreso = fechaEgreso; }
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
}