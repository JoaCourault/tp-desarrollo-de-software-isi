package com.isi.desa.Service.Interfaces;

import com.isi.desa.Dto.Factura.*;

public interface IFaucturacionService {
    ObtenerResponsablesDePagoParaFacturacionResult obtenerResponsablesDePagoParaFacturacion(ObtenerResponsablesDePagoParaFacturacionRequest request);
    GenerarFacturacionHabitacionResult generarFacturacionParaHabitacion(GenerarFacturacionHabitacionRequest request);
    ConfirmarFacturacionResult confirmarFacturacion(ConfirmarFacturacionRequest request);
}
