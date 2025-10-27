package com.isi.desa.Dao.Interfaces;
import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Exceptions.HuespedConEstadiaAsociadasException;
import com.isi.desa.Model.Entities.Huesped.Huesped;

import java.util.List;

public interface IHuespedDAO {

    Huesped crear(HuespedDTO huesped);

    Huesped modificar(HuespedDTO huesped);

    Huesped eliminar(String idHuesped);

    Huesped obtenerHuesped(String DNI);

    List<Huesped> leerHuespedes();

    Huesped getById(String id);
}
