package com.isi.desa.Dto.Factura;

import java.time.LocalDateTime;

public class GenerarFacturacionHabitacionRequest {
    public String idHabitacion;
    public LocalDateTime momentoDeFecturacion;
    public String idResponsableDePago;
    public Boolean cobroATerceros;
}
