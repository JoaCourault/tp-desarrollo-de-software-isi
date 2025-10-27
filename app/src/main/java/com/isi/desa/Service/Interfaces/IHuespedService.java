package com.isi.desa.Service.Interfaces;

import com.isi.desa.Dto.Huesped.*;

import java.util.List;

public interface IHuespedService{

    HuespedDTO crear(HuespedDTO huespedDTO);

    HuespedDTO modificar(HuespedDTO huespedDTO);

    BajaHuespedResultDTO eliminar(BajaHuespedRequestDTO huespedDTO);

    BuscarHuespedResultDTO buscarHuesped(BuscarHuespedRequestDTO requestDTO);
}
