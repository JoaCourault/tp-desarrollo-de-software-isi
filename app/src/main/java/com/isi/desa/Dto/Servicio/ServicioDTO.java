package com.isi.desa.Dto.Servicio;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "tipo"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ServicioDTO.BarDTO.class, name = "Bar"),
        @JsonSubTypes.Type(value = ServicioDTO.SaunaDTO.class, name = "Sauna"),
        @JsonSubTypes.Type(value = ServicioDTO.LavadoYPlanchadoDTO.class, name = "LavadoYPlanchado")
})
public abstract class ServicioDTO {
    public String id;
    public LocalDateTime fecha;
    public BigDecimal precio;

    public static class BarDTO extends ServicioDTO {
        public String detalle;
    }
    public static class SaunaDTO extends ServicioDTO {
        public Integer cantidadPersonas;
    }
    public static class LavadoYPlanchadoDTO extends ServicioDTO {
        public Integer cantidadPrendas;
    }
}
