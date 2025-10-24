package com.isi.desa.Utils.Mappers;

import com.isi.desa.Dto.Estadia.EstadiaDTO;
import com.isi.desa.Model.Entities.Estadia.Estadia;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EstadiaMapper {
    public static EstadiaDTO entityToDto(Estadia e) {
        EstadiaDTO dto = new EstadiaDTO();
        dto.idEstadia = e.getIdEstadia();
        dto.valorTotalEstadia = e.getValorTotalEstadia();
        dto.checkIn = e.getCheckIn();
        dto.checkOut = e.getCheckOut();
        dto.cantNoches = e.getCantNoches();
        return dto;
    }
    public static Estadia dtoToEntity(EstadiaDTO dto) {
        Estadia e = new Estadia();
        e.setIdEstadia(dto.idEstadia);
        e.setValorTotalEstadia(dto.valorTotalEstadia);
        e.setCheckIn(dto.checkIn);
        e.setCheckOut(dto.checkOut);
        e.setCantNoches(dto.cantNoches);
        return e;
    }
}