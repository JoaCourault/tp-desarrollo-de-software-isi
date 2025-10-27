package com.isi.desa.Dto.Huesped;

import com.isi.desa.Dto.Resultado;

public class BajaHuespedResultDTO {
    public Resultado resultado;
    public HuespedDTO huesped = null;
    public BajaHuespedResultDTO() {
        this.resultado = new Resultado();
    }
}
