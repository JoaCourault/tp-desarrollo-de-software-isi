package com.isi.desa.Dao.Interfaces;

import com.isi.desa.Dto.Reserva.ReservaDTO;
import com.isi.desa.Model.Entities.Reserva.Reserva;

import java.time.LocalDate;
import java.util.List;

public interface IReservaDAO {

    // Obtener entidad por ID
    Reserva getById(String id);

    // Actualizar a partir de DTO
    Reserva update(ReservaDTO dto);

    // Guardar entidad directamente (por si ya tenés la entity armada)
    Reserva save(Reserva r);

    // Borrar por ID
    void deleteById(String id);

    // Buscar reservas que se solapan en un rango para una habitación
    List<Reserva> buscarReservasSolapadas(String idHabitacion, LocalDate desde, LocalDate hasta);

    // Proyecciones en DTO
    List<ReservaDTO> findAllDTO();
    ReservaDTO findByIdDTO(String id);
}
