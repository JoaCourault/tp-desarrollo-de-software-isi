package com.isi.desa.Dto.Pago;

import com.isi.desa.Dto.Factura.FacturaDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PagoDTO {
    public String idPago;
    public BigDecimal valor;
    public LocalDateTime fecha;
    public FacturaDTO factura;
}
