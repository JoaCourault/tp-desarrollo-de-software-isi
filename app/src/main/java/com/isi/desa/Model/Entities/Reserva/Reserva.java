package com.isi.desa.Model.Entities.Reserva;

import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Model.Entities.Habitacion.HabitacionEntity; // Asegúrate de tener esta entidad
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "reserva")
public class Reserva {

    @Id
    @Column(name = "id_reserva", nullable = false)
    private String idReserva;

    // RELACIONES (Foreign Keys)

    // Mapea la columna "id_huesped" de tu tabla
    @ManyToOne
    @JoinColumn(name = "id_huesped", referencedColumnName = "id_huesped")
    private Huesped huesped;

    // Mapea la columna "id_habitacion" de tu tabla
    @ManyToOne
    @JoinColumn(name = "id_habitacion", referencedColumnName = "id_habitacion") // Asumiendo que HabitacionEntity tiene este ID
    private HabitacionEntity habitacion;

    // CAMPOS DE FECHA Y ESTADO

    @Column(name = "fecha_ingreso")
    private LocalDate fechaIngreso;

    @Column(name = "fecha_egreso")
    private LocalDate fechaEgreso;

    @Column(name = "fecha_desde", nullable = false)
    private LocalDate fechaDesde;

    @Column(name = "fecha_hasta", nullable = false)
    private LocalDate fechaHasta;

    @Column(name = "estado", nullable = false)
    private String estado;


    // --- CONSTRUCTORES ---

    public Reserva() {
        // Generar ID automáticamente si está vacío al crear
        this.idReserva = UUID.randomUUID().toString();
    }

    public Reserva(Huesped huesped, HabitacionEntity habitacion, LocalDate fechaDesde, LocalDate fechaHasta) {
        this.idReserva = UUID.randomUUID().toString();
        this.huesped = huesped;
        this.habitacion = habitacion;
        this.fechaDesde = fechaDesde;
        this.fechaHasta = fechaHasta;
        // Valores por defecto sugeridos para las otras columnas:
        this.fechaIngreso = fechaDesde;
        this.fechaEgreso = fechaHasta;
        this.estado = "RESERVADA";
    }

    // --- GETTERS Y SETTERS ---

    public String getIdReserva() { return idReserva; }
    public void setIdReserva(String idReserva) { this.idReserva = idReserva; }

    public Huesped getHuesped() { return huesped; }
    public void setHuesped(Huesped huesped) { this.huesped = huesped; }

    public HabitacionEntity getHabitacion() { return habitacion; }
    public void setHabitacion(HabitacionEntity habitacion) { this.habitacion = habitacion; }

    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public LocalDate getFechaEgreso() { return fechaEgreso; }
    public void setFechaEgreso(LocalDate fechaEgreso) { this.fechaEgreso = fechaEgreso; }

    public LocalDate getFechaDesde() { return fechaDesde; }
    public void setFechaDesde(LocalDate fechaDesde) { this.fechaDesde = fechaDesde; }

    public LocalDate getFechaHasta() { return fechaHasta; }
    public void setFechaHasta(LocalDate fechaHasta) { this.fechaHasta = fechaHasta; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}