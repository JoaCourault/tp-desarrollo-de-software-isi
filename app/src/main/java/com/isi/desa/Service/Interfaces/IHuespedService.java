package com.isi.desa.Service.Interfaces;

import com.isi.desa.Dto.Huesped.*;
import com.isi.desa.Exceptions.HuespedDuplicadoException;

import java.util.List;

public interface IHuespedService{

    HuespedDTO crear(HuespedDTO huespedDTO) throws HuespedDuplicadoException;

    HuespedDTO modificar(HuespedDTO huespedDTO);

    BajaHuespedResultDTO eliminar(BajaHuespedRequestDTO huespedDTO);

    BuscarHuespedResultDTO buscarHuesped(BuscarHuespedRequestDTO requestDTO);

    ModificarHuespedResultDTO modificar(ModificarHuespedRequestDTO requestDTO);

}
