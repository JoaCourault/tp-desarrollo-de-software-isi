package com.isi.desa.Model.Entities.Tipodocumento;

import com.fasterxml.jackson.annotation.JsonAlias;

public class TipoDocumento {
    @JsonAlias("id")
    private String tipoDocumento;
    private String descripcion; // este se corresponde con "descripcion"

    public TipoDocumento() {}

    public TipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public TipoDocumento(String tipoDocumento, String descripcion) {
        this.tipoDocumento = tipoDocumento;
        this.descripcion = descripcion;
    }

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
