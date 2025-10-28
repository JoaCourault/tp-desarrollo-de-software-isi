package com.isi.desa.Dto.Huesped;

import com.isi.desa.Dto.Resultado;

public class AltaHuespedResultDTO {
    public Resultado resultado;
    public HuespedDTO huesped; // <<< NUEVO

    public AltaHuespedResultDTO() {
        this.resultado = new Resultado();
    }
}
