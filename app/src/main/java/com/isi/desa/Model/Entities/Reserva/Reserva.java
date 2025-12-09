package com.isi.desa.Model.Entities.Reserva;

import com.isi.desa.Model.Entities.Habitacion.Habitacion;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "reserva")
public class Reserva {

    @Id
    @Column(name = "id_reserva", nullable = false)
    private String idReserva;

    @Column(name = "fecha_ingreso")
    private LocalDate fechaIngreso; // En SQL es DATE, usamos LocalDate

    @Column(name = "fecha_egreso")
    private LocalDate fechaEgreso;

    // Relación con Habitación (Según tu script SQL y lógica del diagrama)
    @ManyToOne
    @JoinColumn(name = "id_habitacion", referencedColumnName = "id_habitacion")
    private Habitacion habitacion;

    // Datos del titular (Strings sueltos según tu Diagrama de Clases y SQL)
    @Column(name = "nombre_huesped")
    private String nombreHuesped;

    @Column(name = "apell_huesped")
    private String apellidoHuesped;

    @Column(name = "tel_huesped")
    private String telefonoHuesped;

    public Reserva() {}

    // Getters y Setters
    public String getIdReserva() { return idReserva; }
    public void setIdReserva(String idReserva) { this.idReserva = idReserva; }

    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public LocalDate getFechaEgreso() { return fechaEgreso; }
    public void setFechaEgreso(LocalDate fechaEgreso) { this.fechaEgreso = fechaEgreso; }

    public Habitacion getHabitacion() { return habitacion; }
    public void setHabitacion(Habitacion habitacion) { this.habitacion = habitacion; }

    public String getNombreHuesped() { return nombreHuesped; }
    public void setNombreHuesped(String nombreHuesped) { this.nombreHuesped = nombreHuesped; }

    public String getApellidoHuesped() { return apellidoHuesped; }
    public void setApellidoHuesped(String apellidoHuesped) { this.apellidoHuesped = apellidoHuesped; }

    public String getTelefonoHuesped() { return telefonoHuesped; }
    public void setTelefonoHuesped(String telefonoHuesped) { this.telefonoHuesped = telefonoHuesped; }
}