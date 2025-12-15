package com.isi.desa.Service.Interfaces;

import com.isi.desa.Dto.Huesped.*;
import com.isi.desa.Exceptions.Huesped.HuespedDuplicadoException;

public interface IHuespedService{
    AltaHuespedResultDTO crear(AltaHuespedRequestDTO huespedDTO);
    BajaHuespedResultDTO eliminar(BajaHuespedRequestDTO huespedDTO);
    BuscarHuespedResultDTO buscarHuesped(BuscarHuespedRequestDTO requestDTO);
    ModificarHuespedResultDTO modificar(ModificarHuespedRequestDTO requestDTO);
}
