package com.isi.desa.Dto.Estadia;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EstadiaDTO {
    public String idEstadia;
    public BigDecimal valorTotalEstadia;
    public LocalDateTime checkIn;
    public LocalDateTime checkOut;
    public Integer cantNoches;
}
