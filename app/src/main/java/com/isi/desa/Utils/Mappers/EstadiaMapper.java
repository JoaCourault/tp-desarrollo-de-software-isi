package com.isi.desa.Utils.Mappers;

import com.isi.desa.Dto.Estadia.EstadiaDTO;
import com.isi.desa.Model.Entities.Estadia.Estadia;
import com.isi.desa.Model.Entities.Habitacion.HabitacionEntity;
import com.isi.desa.Model.Entities.Huesped.Huesped;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EstadiaMapper {

    public static EstadiaDTO entityToDTO(Estadia e) {
        if (e == null) return null;
        EstadiaDTO dto = new EstadiaDTO();
        dto.idEstadia = e.getIdEstadia();
        dto.valorTotalEstadia = e.getValorTotalEstadia();
        dto.checkIn = e.getCheckIn();
        dto.checkOut = e.getCheckOut();
        dto.cantNoches = e.getCantNoches();
        dto.idFactura = e.getIdFactura();

        if (e.getReserva() != null) dto.idReserva = e.getReserva().getIdReserva();
        if (e.getHuespedTitular() != null) dto.idHuespedTitular = e.getHuespedTitular().getIdHuesped();

        // Huéspedes
        if (e.getListaHuespedes() != null) {
            dto.idsOcupantes = e.getListaHuespedes().stream()
                    .map(Huesped::getIdHuesped).collect(Collectors.toList());
        }

        // Habitaciones
        if (e.getListaHabitaciones() != null) {
            dto.idsHabitaciones = e.getListaHabitaciones().stream()
                    .map(HabitacionEntity::getIdHabitacion).collect(Collectors.toList());
        }

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