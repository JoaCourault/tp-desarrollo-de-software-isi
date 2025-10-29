package com.isi.desa.Model.Entities.Tipodocumento;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TipoDocumento {

    private String tipoDocumento;

    public TipoDocumento() {}

    public TipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    // === GETTERS ===
    public String getTipoDocumento() { return tipoDocumento; }

    // === SETTERS ===
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    // Soporta el JSON de tipoDocumento.json -> campo "id"
    @JsonProperty("id")
    public void setIdFromJson(String id) {
        this.tipoDocumento = id;
    }

    // Soporta el JSON de huesped.json -> campo "tipoDocumento"
    @JsonProperty("tipoDocumento")
    public void setTipoDocumentoFromJson(String id) {
        this.tipoDocumento = id;
    }
}
