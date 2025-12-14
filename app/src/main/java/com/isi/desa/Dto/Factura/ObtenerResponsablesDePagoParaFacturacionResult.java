package com.isi.desa.Dto.Factura;

import com.isi.desa.Dto.ResponsableDePago.ResponsableDePagoDTO;
import com.isi.desa.Dto.Resultado;

import java.util.List;

public class ObtenerResponsablesDePagoParaFacturacionResult {
    public Resultado resultado;
    public List<ResponsableDePagoDTO> responsablesDePago;

    public ObtenerResponsablesDePagoParaFacturacionResult() {
        resultado = new Resultado();
    }
}
