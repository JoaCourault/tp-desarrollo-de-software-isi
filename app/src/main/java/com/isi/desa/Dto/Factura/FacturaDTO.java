package com.isi.desa.Dto.Factura;

import com.isi.desa.Dto.Estadia.EstadiaDTO;
import com.isi.desa.Dto.NotaDeCredito.NotaDeCreditoDTO;
import com.isi.desa.Dto.Pago.PagoDTO;
import com.isi.desa.Dto.ResponsableDePago.ResponsableDePagoDTO;
import com.isi.desa.Dto.Servicio.ServicioDTO;

import java.math.BigDecimal;
import java.util.List;

public class FacturaDTO {
    public String idFactura;
    public String detalle;
    public BigDecimal total;
    public String nombre;
    public ResponsableDePagoDTO responsableDePago;
    public PagoDTO pago;
    public NotaDeCreditoDTO notaDeCredito;
    public List<EstadiaDTO> estadias;
    public List<ServicioDTO> servicios;
}
