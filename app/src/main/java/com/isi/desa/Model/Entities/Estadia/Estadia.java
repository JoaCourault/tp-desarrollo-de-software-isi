package com.isi.desa.Model.Entities.Estadia;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Estadia {
    private String idEstadia;
    private BigDecimal valorTotalEstadia;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private Integer cantNoches;

    public Estadia() {}

    public Estadia(String idEstadia, BigDecimal valorTotalEstadia, LocalDateTime checkIn, LocalDateTime checkOut, Integer cantNoches) {
        this.idEstadia = idEstadia;
        this.valorTotalEstadia = valorTotalEstadia;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.cantNoches = cantNoches;
    }

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
}
