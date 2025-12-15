package com.isi.desa.Model.Entities.Estadia;

import com.isi.desa.Model.Entities.Factura.Factura;
import com.isi.desa.Model.Entities.Habitacion.Habitacion;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Model.Entities.Reserva.Reserva;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Estadia")
public class Estadia {

    @Id
    @GeneratedValue(generator = "id_estadia")
    @GenericGenerator(name = "id_estadia", strategy = "uuid2")
    @Column(name = "id_estadia", nullable = false, updatable = false)
    private String idEstadia;

    @Column(name = "valor_total_estadia")
    private BigDecimal valorTotalEstadia;

    @Column(name = "check_in")
    private LocalDateTime checkIn;

    @Column(name = "check_out")
    private LocalDateTime checkOut;

    @Column(name = "cant_noches")
    private Integer cantNoches;

    @OneToOne
    @JoinColumn(name = "id_reserva", referencedColumnName = "id_reserva", nullable = false)
    private Reserva reserva;

    @ManyToMany(mappedBy = "estadias")
    private List<Factura> facturas;

    @ManyToMany
    @JoinTable(
            name = "habitacion_estadia",
            joinColumns = @JoinColumn(name = "id_estadia"),
            inverseJoinColumns = @JoinColumn(name = "id_habitacion")
    )
    private List<Habitacion> habitaciones;

    @ManyToMany
    @JoinTable(
            name = "huesped_estadia",
            joinColumns = @JoinColumn(name = "id_estadia"),
            inverseJoinColumns = @JoinColumn(name = "id_huesped")
    )
    private List<Huesped> huespedesHospedados;

    public Estadia() {}

    // Getters y setters
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

    public List<Huesped> getHuespedesHospedados() { return huespedesHospedados; }
    public void setHuespedesHospedados(List<Huesped> huespedesHospedados) { this.huespedesHospedados = huespedesHospedados; }

    public List<Factura> getFacturas() { return facturas; }
    public void setFacturas(List<Factura> facturas) { this.facturas = facturas; }

    public List<Habitacion> getHabitaciones() {
        return habitaciones;
    }

    public void setHabitaciones(List<Habitacion> habitaciones) {
        this.habitaciones = habitaciones;
    }
}