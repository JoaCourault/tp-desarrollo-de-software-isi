package com.isi.desa.Service.Interfaces;

import com.isi.desa.Dto.Huesped.HuespedDTO;
import java.util.List;

public interface IHuespedService {

    HuespedDTO crear(HuespedDTO huespedDTO);

    HuespedDTO modificar(HuespedDTO huespedDTO);

    HuespedDTO eliminar(HuespedDTO huespedDTO);

    List<HuespedDTO> buscarHuesped(HuespedDTO filtros);
}
