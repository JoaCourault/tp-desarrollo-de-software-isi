package com.isi.desa.Dto.Factura;

import java.time.LocalDateTime;
import java.util.Optional;

public class ObtenerResponsablesDePagoParaFacturacionRequest {
    public String idHabitacion;
    public String idServicio;
    public LocalDateTime momentoDeFecturacion;
    public Optional<String> idTerceroACobrar;
}
