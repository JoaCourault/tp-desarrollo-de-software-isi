package com.isi.desa.Dao.Interfaces;

import com.isi.desa.Dto.Direccion.DireccionDTO;
import com.isi.desa.Model.Entities.Direccion.Direccion;

public interface IDireccionDAO {

    Direccion crear(DireccionDTO direccion);

    Direccion modificar(DireccionDTO direccion);

    Direccion eliminar(DireccionDTO direccion);

    Direccion obtener(DireccionDTO direccion);

    java.util.List<Direccion> obtenerTodas();

    Direccion getById(String id);
}