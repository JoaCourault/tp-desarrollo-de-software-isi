package com.isi.desa.Service.Interfaces;

import com.isi.desa.Dto.Huesped.*;
import com.isi.desa.Exceptions.Huesped.HuespedDuplicadoException;
import com.isi.desa.Model.Entities.Huesped.Huesped;

public interface IHuespedService {
    HuespedDTO crear(HuespedDTO huespedDTO) throws HuespedDuplicadoException;
    BajaHuespedResultDTO eliminar(BajaHuespedRequestDTO requestDTO);
    BuscarHuespedResultDTO buscarHuesped(BuscarHuespedRequestDTO req);
    ModificarHuespedResultDTO modificar(ModificarHuespedRequestDTO request);

    // --- NUEVO MÉTODO PARA CU02 ---
    Huesped getById(String id);
}