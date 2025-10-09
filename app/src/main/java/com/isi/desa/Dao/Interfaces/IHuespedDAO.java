package com.isi.desa.Dao.Interfaces;
import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Model.Entities.Huesped.Huesped;

public interface IHuespedDAO {

    Huesped crear(HuespedDTO huesped);

    Huesped modificar(HuespedDTO huesped);

    Huesped eliminar(HuespedDTO huesped);

    Huesped obtenerHuesped(String DNI);

}
