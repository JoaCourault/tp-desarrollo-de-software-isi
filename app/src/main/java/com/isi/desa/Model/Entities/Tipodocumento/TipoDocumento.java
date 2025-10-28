package com.isi.desa.Model.Entities.Tipodocumento;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TipoDocumento {

    private String tipoDocumento;
    private String descripcion;

    public TipoDocumento() {}

    public TipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public TipoDocumento(String tipoDocumento, String descripcion) {
        this.tipoDocumento = tipoDocumento;
        this.descripcion = descripcion;
    }

    // === GETTERS ===
    public String getTipoDocumento() { return tipoDocumento; }
    public String getDescripcion() { return descripcion; }

    // === SETTERS ===
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    // ✅ Soporta el JSON de tipoDocumento.json -> campo "id"
    @JsonProperty("id")
    public void setIdFromJson(String id) {
        this.tipoDocumento = id;
    }

    // ✅ Soporta el JSON de huesped.json -> campo "tipoDocumento"
    @JsonProperty("tipoDocumento")
    public void setTipoDocumentoFromJson(String id) {
        this.tipoDocumento = id;
    }

    // ✅ Setter normal (para cuando se crea desde DTO)
    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }
}
