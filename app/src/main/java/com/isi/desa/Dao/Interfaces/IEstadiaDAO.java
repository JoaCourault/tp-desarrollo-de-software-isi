package com.isi.desa.Dao.Interfaces;
import com.isi.desa.Dto.Estadia.EstadiaDTO;
import com.isi.desa.Model.Entities.Estadia.*;

import java.util.List;

public interface IEstadiaDAO {
    Estadia crear(EstadiaDTO estadia);
    Estadia modificar(EstadiaDTO estadia);
    Estadia eliminar(EstadiaDTO estadia);
    Estadia obtener(EstadiaDTO estadia);

    List<Estadia> leerEstadias();

    /*Verifica si el huesped con el ID especificado se ha alojado alguna vez.*/

    List<String> obtenerIdsHuespedesConEstadias(String idHuesped);
}