package com.isi.desa.Model.Entities.Estadia;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.isi.desa.Model.Entities.Habitacion.HabitacionEntity;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Model.Entities.Reserva.Reserva;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "estadia")
public class Estadia {

    @Id
    @Column(name = "id_estadia", nullable = false)
    private String idEstadia;

    // Relación con Reserva (0..1)
    @OneToOne
    @JoinColumn(name = "id_reserva", referencedColumnName = "id_reserva", unique = true, nullable = true)
    private Reserva reserva;
    // Relación con Titular (Responsable)
    @ManyToOne
    @JoinColumn(name = "id_huesped_titular", referencedColumnName = "id_huesped", nullable = false)
    private Huesped huespedTitular;

    // --- NUEVO: LISTA DE TODOS LOS HUÉSPEDES (Tabla Intermedia) ---
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "huesped_estadia", // Nombre de tu tabla en la BD
            joinColumns = @JoinColumn(name = "id_estadia"),
            inverseJoinColumns = @JoinColumn(name = "id_huesped")
    )
    @JsonIgnore
    private List<Huesped> listaHuespedes = new ArrayList<>();

    @Column(name = "valor_total_estadia")
    private Float valorTotalEstadia;

    @Column(name = "check_in")
    private LocalDateTime checkIn;

    @Column(name = "check_out")
    private LocalDateTime checkOut;

    @Column(name = "cant_noches")
    private Integer cantNoches;

    @Column(name = "id_factura")
    private String idFactura;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "habitacion_estadia",
            joinColumns = @JoinColumn(name = "id_estadia"),
            inverseJoinColumns = @JoinColumn(name = "id_habitacion")
    )
    @JsonIgnore
    public List<HabitacionEntity> listaHabitaciones;

    public Estadia() {}

    // --- Getters y Setters ---

    public String getIdEstadia() { return idEstadia; }
    public void setIdEstadia(String idEstadia) { this.idEstadia = idEstadia; }

    public Reserva getReserva() { return reserva; }
    public void setReserva(Reserva reserva) { this.reserva = reserva; }

    public Huesped getHuespedTitular() {
        return huespedTitular;
    }

    public void setHuespedTitular(Huesped huespedTitular) {
        this.huespedTitular = huespedTitular;
    }


    public List<Huesped> getListaHuespedes() { return listaHuespedes; }
    public void setListaHuespedes(List<Huesped> listaHuespedes) { this.listaHuespedes = listaHuespedes; }

    public Float getValorTotalEstadia() { return valorTotalEstadia; }
    public void setValorTotalEstadia(Float valorTotalEstadia) { this.valorTotalEstadia = valorTotalEstadia; }

    public LocalDateTime getCheckIn() { return checkIn; }
    public void setCheckIn(LocalDateTime checkIn) { this.checkIn = checkIn; }

    public LocalDateTime getCheckOut() { return checkOut; }
    public void setCheckOut(LocalDateTime checkOut) { this.checkOut = checkOut; }

    public Integer getCantNoches() { return cantNoches; }
    public void setCantNoches(Integer cantNoches) { this.cantNoches = cantNoches; }

    public String getIdFactura() { return idFactura; }
    public void setIdFactura(String idFactura) { this.idFactura = idFactura; }

    public List<HabitacionEntity> getListaHabitaciones() { return listaHabitaciones; }
    public void setListaHabitaciones(List<HabitacionEntity> listaHabitaciones) { this.listaHabitaciones = listaHabitaciones; }
}