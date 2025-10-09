package com.isi.desa.Dao.Interfaces;

import com.isi.desa.Dto.TipoDocumento.TipoDocumentoDTO;
import com.isi.desa.Model.Entities.Tipodocumento.TipoDocumento;

public interface ITipoDocumentoDAO {
    TipoDocumento crear(TipoDocumentoDTO tipoDocumento);
    TipoDocumento modificar(TipoDocumentoDTO tipoDocumento);
    TipoDocumento eliminar(TipoDocumentoDTO tipoDocumento);
    TipoDocumento obtener(String tipoDocumento);
}
