package com.isi.desa.Dao.Interfaces;
import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Exceptions.Huesped.HuespedDuplicadoException;
import com.isi.desa.Model.Entities.Estadia.Estadia;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IHuespedDAO {

    Huesped crear(HuespedDTO huesped) throws HuespedDuplicadoException;

    Huesped modificar(HuespedDTO huesped);

    Huesped eliminar(String idHuesped);

    Huesped obtenerHuesped(String DNI);

    List<Huesped> buscarHuesped(HuespedDTO filtro);

    List<Huesped> leerHuespedes();

    Huesped getById(String id);

    List<Estadia> obtenerEstadiasDeHuesped(String idHuesped);

    void agregarEstadiaAHuesped(String idHuesped, String idEstadia);
}
