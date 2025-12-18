package com.isi.desa.Model.Entities.Tipodocumento;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "TipoDocumento")
public class TipoDocumento {

    @Id
    @Column(name = "tipoDocumento", nullable = false, unique = true)
    private String tipoDocumento;

    public TipoDocumento() {}

    public TipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getTipoDocumento() { return tipoDocumento; }

    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
}
