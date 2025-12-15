package com.isi.desa.Dto.Huesped;

import com.isi.desa.Dto.Resultado;

import java.util.List;

public class BuscarHuespedResultDTO {
    public Resultado resultado;

    //Ahora es una lista de DTOs, no de Entidades
    public List<HuespedDTO> huespedesEncontrados;
}