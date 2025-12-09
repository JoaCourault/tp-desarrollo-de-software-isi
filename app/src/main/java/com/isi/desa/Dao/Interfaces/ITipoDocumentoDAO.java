package com.isi.desa.Dao.Interfaces;

import com.isi.desa.Model.Entities.Tipodocumento.TipoDocumento;

import java.util.List;

public interface ITipoDocumentoDAO {
    List<TipoDocumento> obtenerTodos();
    TipoDocumento obtener(String tipoDocumento);
}
