package com.isi.desa.Dto.Huesped;

import com.isi.desa.Dto.Resultado;
import java.util.List;
import java.util.ArrayList;

public class BuscarHuespedResultDTO {
    public Resultado resultado;

    // CAMBIO IMPORTANTE: Usar HuespedDTO en lugar de Huesped (Entidad)
    public List<HuespedDTO> huespedesEncontrados;

    public BuscarHuespedResultDTO() {
        this.resultado = new Resultado();
        this.huespedesEncontrados = new ArrayList<>();
    }
}