package com.isi.desa.Dao.Interfaces;

import com.isi.desa.Dto.Direccion.DireccionDTO;
import com.isi.desa.Model.Entities.Direccion.Direccion;

public interface IDireccionDAO {

    Direccion crear(DireccionDTO dto);

    Direccion modificar(DireccionDTO dto);

    Direccion eliminar(DireccionDTO dto);

    Direccion obtener(DireccionDTO dto);

}
