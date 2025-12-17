package com.isi.desa.Utils.Mappers;

import com.isi.desa.Dto.Estadia.EstadiaDTO;
import com.isi.desa.Model.Entities.Estadia.Estadia;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EstadiaMapper {

    public EstadiaDTO entityToDto(Estadia e) {
        if (e == null) return null;
        EstadiaDTO dto = new EstadiaDTO();
        dto.idEstadia = e.getIdEstadia();
        dto.valorTotalEstadia = e.getValorTotalEstadia();
        dto.checkIn = e.getCheckIn();
        dto.checkOut = e.getCheckOut();
        dto.cantNoches = e.getCantNoches();
        dto.huespedesHospedados = HuespedMapper.entityListToDtoList(e.getHuespedesHospedados());
        dto.habitaciones = HabitacionMapper.entityListToDtoList(e.getHabitaciones());
        return dto;
    }

    public Estadia dtoToEntity(EstadiaDTO dto) {
        if (dto == null) return null;
        Estadia e = new Estadia();
        e.setIdEstadia(dto.idEstadia);
        e.setValorTotalEstadia(dto.valorTotalEstadia);
        e.setCheckIn(dto.checkIn);
        e.setCheckOut(dto.checkOut);
        e.setCantNoches(dto.cantNoches);
        e.setHuespedesHospedados(HuespedMapper.dtoListToEntitiesList(dto.huespedesHospedados));
        e.setHabitaciones(HabitacionMapper.dtoLisToEntitiesList(dto.habitaciones));
        return e;
    }

    public List<Estadia> dtoLisToEntitiesList(List<EstadiaDTO> dto) {
        if (dto == null) return new ArrayList<>();
        return dto.stream().map(this::dtoToEntity).collect(Collectors.toList());
    }

    public List<EstadiaDTO> entityListToDtoList(List<Estadia> estadias) {
        if (estadias == null) return new ArrayList<>();
        return estadias.stream().map(this::entityToDto).collect(Collectors.toList());
    }
}