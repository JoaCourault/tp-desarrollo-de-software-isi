package com.isi.desa.Dto.TipoDocumento;

import com.fasterxml.jackson.annotation.JsonAlias;

public class TipoDocumentoDTO {
    @JsonAlias ("id")
    public String tipoDocumento;
    public String descripcion;
}
