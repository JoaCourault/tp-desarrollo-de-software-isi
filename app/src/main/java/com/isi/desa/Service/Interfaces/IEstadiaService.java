package com.isi.desa.Service.Interfaces;

import com.isi.desa.Dto.Estadia.CrearEstadiaRequestDTO;
import com.isi.desa.Dto.Estadia.EstadiaDTO;

public interface IEstadiaService {

    public EstadiaDTO ocuparHabitacion(CrearEstadiaRequestDTO request);

}
