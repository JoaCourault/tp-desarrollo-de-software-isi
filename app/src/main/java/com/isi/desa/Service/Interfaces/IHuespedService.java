package com.isi.desa.Service.Interfaces;

import com.isi.desa.Dto.Huesped.*;
import com.isi.desa.Exceptions.Huesped.HuespedDuplicadoException;
import com.isi.desa.Model.Entities.Huesped.Huesped;

public interface IHuespedService{
    HuespedDTO crear(HuespedDTO huespedDTO, Boolean aceptarIgualmente) throws HuespedDuplicadoException;

    HuespedDTO crear(HuespedDTO huespedDTO) throws HuespedDuplicadoException;
    BajaHuespedResultDTO eliminar(BajaHuespedRequestDTO huespedDTO);
    BuscarHuespedResultDTO buscarHuesped(BuscarHuespedRequestDTO requestDTO);
    ModificarHuespedResultDTO modificar(ModificarHuespedRequestDTO requestDTO);

    Huesped getById(String id);
}