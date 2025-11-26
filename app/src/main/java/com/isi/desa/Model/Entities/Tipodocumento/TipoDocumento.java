package com.isi.desa.Model.Entities.Tipodocumento;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tipo_documento")
public class TipoDocumento {

    @Id
    @Column(name = "id")
    private String tipoDocumento;

    public TipoDocumento() {}

    public TipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }


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
