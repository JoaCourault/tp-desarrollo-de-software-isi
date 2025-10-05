package com.isi.desa.Dto.Pago;

import com.isi.desa.Dto.MetodoDePago.MetodoDePagoDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PagoDTO {
    public BigDecimal valor;
    public LocalDateTime fecha;
    public List<MetodoDePagoDTO> metodosDePago;
}
