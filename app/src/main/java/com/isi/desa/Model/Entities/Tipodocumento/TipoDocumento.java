package com.isi.desa.Model.Entities.Tipodocumento;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "tipo_documento")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TipoDocumento implements Serializable {

    @Id
    @Column(name = "tipo_documento", nullable = false, length = 50)
    @JsonProperty("tipoDocumento")              // cuando venga como "tipoDocumento"
    @JsonAlias({"id", "tipo_documento"})        // cuando venga como "id" o "tipo_documento"
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
}
