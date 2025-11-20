package com.isi.desa.Utils.Mappers;

import com.isi.desa.Dto.Estadia.EstadiaDTO;
import com.isi.desa.Model.Entities.Estadia.Estadia;
import jdk.dynalink.linker.LinkerServices;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    public static List<Estadia> dtoLisToEntitiesList(List<EstadiaDTO> dto) {
        List<Estadia> estadias = new ArrayList<>();
        for (EstadiaDTO e : dto) {
            estadias.add(dtoToEntity(e));
        }
        return estadias;
    }
    public static List<EstadiaDTO> entityListToDtoList(List<Estadia> estadias) {
        List<EstadiaDTO> dtoList = new ArrayList<>();
        for (Estadia e : estadias) {
            dtoList.add(entityToDto(e));
        }
        return dtoList;
    }
}