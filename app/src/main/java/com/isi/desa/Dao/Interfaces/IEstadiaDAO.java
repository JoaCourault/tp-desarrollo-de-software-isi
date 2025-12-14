package com.isi.desa.Dao.Interfaces;
import com.isi.desa.Dto.Estadia.EstadiaDTO;
import com.isi.desa.Model.Entities.Estadia.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IEstadiaDAO {
    EstadiaDTO save(Estadia estadia);
    Optional<Estadia> findById(String id);
    List<Estadia> findAll();
    void deleteById(String id);
    List<Estadia> findByIdHabitacionAndMoment(String idHabitacion, LocalDateTime moment);
}