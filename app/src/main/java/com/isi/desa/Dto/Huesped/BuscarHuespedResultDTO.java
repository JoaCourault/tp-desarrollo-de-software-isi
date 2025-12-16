package com.isi.desa.Dto.Huesped;

import com.isi.desa.Dto.Resultado;
import com.isi.desa.Model.Entities.Huesped.Huesped;

import java.util.List;
import java.util.ArrayList;

public class BuscarHuespedResultDTO {
    public Resultado resultado;
    public List<HuespedDTO> huespedesEncontrados;

    public BuscarHuespedResultDTO() {
        this.resultado = new Resultado();
        this.huespedesEncontrados = new ArrayList<>();
    }
}