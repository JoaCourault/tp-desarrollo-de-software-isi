package com.isi.desa.Service.Interfaces;

import com.isi.desa.Dto.Estadia.CrearEstadiaRequestDTO;
import com.isi.desa.Dto.Estadia.EstadiaDTO;
import com.isi.desa.Dto.Estadia.EstadiaDetalleDTO;

public interface IEstadiaService {

    EstadiaDTO ocuparHabitacion(CrearEstadiaRequestDTO request);
    EstadiaDetalleDTO buscarDetallePorHabitacion(Integer numero);

}
