package com.isi.desa.Model.Entities.Estadia;

import com.isi.desa.Model.Entities.Reserva.Reserva;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Estadia")
public class Estadia {

    @Id
    @GeneratedValue(generator = "id_estadia")
    @GenericGenerator(name = "id_estadia", strategy = "uuid2")
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

    @OneToOne
    @JoinColumn(name = "id_reserva", referencedColumnName = "id_reserva")
    @Column(name = "id_reserva", nullable = true)
    private Reserva reserva;

    @Column(name = "id_factura", nullable = false)
    private String idFactura;

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

    public String getIdReserva() { return idReserva; }
    public void setIdReserva(String idReserva) { this.idReserva = idReserva; }

    public String getIdFactura() { return idFactura; }
    public void setIdFactura(String idFactura) { this.idFactura = idFactura; }
}
