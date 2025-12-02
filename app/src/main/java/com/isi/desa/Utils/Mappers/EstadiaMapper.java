package com.isi.desa.Utils.Mappers;

import com.isi.desa.Dto.Estadia.EstadiaDTO;
import com.isi.desa.Model.Entities.Estadia.Estadia;
import java.util.ArrayList;
import java.util.List;

public class EstadiaMapper {

    public static EstadiaDTO entityToDTO(Estadia e) {
        if (e == null) return null;
        EstadiaDTO dto = new EstadiaDTO();
        dto.idEstadia = e.getIdEstadia();
        dto.valorTotalEstadia = e.getValorTotalEstadia();
        dto.checkIn = e.getCheckIn();
        dto.checkOut = e.getCheckOut();
        dto.cantNoches = e.getCantNoches();
        dto.idReserva = e.getIdReserva();
        dto.idFactura = e.getIdFactura();
        return dto;
    }

    public static Estadia dtoToEntity(EstadiaDTO dto) {
        if (dto == null) return null;
        Estadia e = new Estadia();
        e.setIdEstadia(dto.idEstadia);
        e.setValorTotalEstadia(dto.valorTotalEstadia);
        e.setCheckIn(dto.checkIn);
        e.setCheckOut(dto.checkOut);
        e.setCantNoches(dto.cantNoches);
        e.setIdReserva(dto.idReserva);
        e.setIdFactura(dto.idFactura);
        return e;
    }

    public static List<EstadiaDTO> entityListToDtoList(List<Estadia> estadias) {
        if (estadias == null) return new ArrayList<>();
        List<EstadiaDTO> dtoList = new ArrayList<>();
        for (Estadia e : estadias) {
            dtoList.add(entityToDTO(e));
        }
        return dtoList;
    }
}