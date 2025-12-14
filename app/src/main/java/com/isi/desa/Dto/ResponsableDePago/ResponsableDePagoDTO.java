package com.isi.desa.Dto.ResponsableDePago;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.isi.desa.Dto.ResponsableDePago.PersonaFisica.PersonaFisicaDTO;
import com.isi.desa.Dto.ResponsableDePago.PersonaJuridica.PersonaJuridicaDTO;
import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "tipo"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PersonaFisicaDTO.class, name = "PERSONA_FISICA"),
        @JsonSubTypes.Type(value = PersonaJuridicaDTO.class, name = "PERSONA_JURIDICA")
})
public abstract class ResponsableDePagoDTO {
    public String idResponsableDePago;
}
