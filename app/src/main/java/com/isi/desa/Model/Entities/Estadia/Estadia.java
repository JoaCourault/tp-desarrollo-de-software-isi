package com.isi.desa.Model.Entities.Estadia;

import com.isi.desa.Model.Entities.Habitacion.Habitacion;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime; // <--- USAMOS LocalDateTime
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "estadia")
public class Estadia {

    @Id
    @Column(name = "id_estadia", nullable = false)
    private String idEstadia;

    @Column(name = "valor_total_estadia")
    private BigDecimal valorTotalEstadia;

    @Column(name = "check_in")
    private LocalDateTime checkIn;

    @Column(name = "check_out")
    private LocalDateTime checkOut;

    @Column(name = "cant_noches")
    private Integer cantNoches;

    @Column(name = "id_reserva")
    private String idReserva;

    @Column(name = "id_factura", nullable = true)
    private String idFactura;

    @ManyToMany
    @JoinTable(
            name = "habitacion_estadia",
            joinColumns = @JoinColumn(name = "id_estadia"),
            inverseJoinColumns = @JoinColumn(name = "id_habitacion")
    )
    private List<Habitacion> habitaciones = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "huesped_estadia",
            joinColumns = @JoinColumn(name = "id_estadia"),
            inverseJoinColumns = @JoinColumn(name = "id_huesped")
    )
    private List<Huesped> huespedes = new ArrayList<>();

    public Estadia() {}

    // GETTERS Y SETTERS
    public String getIdEstadia() { return idEstadia; }
    public void setIdEstadia(String idEstadia) { this.idEstadia = idEstadia; }

    public BigDecimal getValorTotalEstadia() { return valorTotalEstadia; }
    public void setValorTotalEstadia(BigDecimal valorTotalEstadia) { this.valorTotalEstadia = valorTotalEstadia; }

    public LocalDateTime getCheckIn() { return checkIn; }
    public void setCheckIn(LocalDateTime checkIn) { this.checkIn = checkIn; }

    public LocalDateTime getCheckOut() { return checkOut; }
    public void setCheckOut(LocalDateTime checkOut) { this.checkOut = checkOut; }

    public Integer getCantNoches() { return cantNoches; }
    public void setCantNoches(Integer cantNoches) { this.cantNoches = cantNoches; }

    public String getIdReserva() { return idReserva; }
    public void setIdReserva(String idReserva) { this.idReserva = idReserva; }

    public String getIdFactura() { return idFactura; }
    public void setIdFactura(String idFactura) { this.idFactura = idFactura; }

    public List<Habitacion> getHabitaciones() { return habitaciones; }
    public void setHabitaciones(List<Habitacion> habitaciones) { this.habitaciones = habitaciones; }

    public List<Huesped> getHuespedes() { return huespedes; }
    public void setHuespedes(List<Huesped> huespedes) { this.huespedes = huespedes; }
}