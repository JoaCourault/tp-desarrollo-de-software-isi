package com.isi.desa.Dao.Interfaces;
import com.isi.desa.Dto.Estadia.EstadiaDTO;
import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Exceptions.Huesped.HuespedDuplicadoException;
import com.isi.desa.Model.Entities.Estadia.*;
import com.isi.desa.Model.Entities.Huesped.Huesped;

import java.util.List;
import java.util.Optional;

public interface IEstadiaDAO {

    Estadia crear(EstadiaDTO estadia);

    Estadia modificar(EstadiaDTO estadia);

    Estadia eliminar(String idEstadia);

    EstadiaDTO save(Estadia estadia);
    Optional<Estadia> findById(String id);
    List<Estadia> findAll();
    void deleteById(String id);
}