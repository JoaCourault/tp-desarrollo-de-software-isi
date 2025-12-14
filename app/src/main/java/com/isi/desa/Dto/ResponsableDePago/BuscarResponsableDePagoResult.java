package com.isi.desa.Dto.ResponsableDePago;

import com.isi.desa.Dto.Resultado;

import java.util.List;

public class BuscarResponsableDePagoResult {
    public Resultado resultado;
    public List<ResponsableDePagoDTO> responsableDePagos;

    public BuscarResponsableDePagoResult() {
        this.resultado = new Resultado();
    }
}
