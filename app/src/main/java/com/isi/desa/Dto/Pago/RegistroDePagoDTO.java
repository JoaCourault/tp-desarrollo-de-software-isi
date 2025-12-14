package com.isi.desa.Dto.Pago;

import com.isi.desa.Dto.MetodoDePago.MetodoDePagoDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RegistroDePagoDTO {
    public String id;
    public PagoDTO pago;
    public MetodoDePagoDTO metodoDePago;
    public LocalDateTime fecha;
    public BigDecimal monto;
}
