package com.isi.desa.Dto.MetodoDePago;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.isi.desa.Model.Enums.EmisorTarjeta;

import java.math.BigDecimal;
import java.time.LocalDate;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "tipo"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MetodoDePagoDTO.EfectivoDTO.class, name = "Efectivo"),
        @JsonSubTypes.Type(value = MetodoDePagoDTO.TarjetaDeCreditoDTO.class, name = "TarjetaDeCredito"),
        @JsonSubTypes.Type(value = MetodoDePagoDTO.TarjetaDeDebitoDTO.class, name = "TarjetaDeDebito"),
        @JsonSubTypes.Type(value = MetodoDePagoDTO.ChequesPropiosDTO.class, name = "ChequesPropios"),
        @JsonSubTypes.Type(value = MetodoDePagoDTO.ChequesDeTercerosDTO.class, name = "ChequesDeTerceros")
})
public abstract class MetodoDePagoDTO {
    public String idMetodoDePago;

    public static class EfectivoDTO extends MetodoDePagoDTO {
        public String divisa;
        public BigDecimal tipoDeCambio;
    }

    public static class TarjetaDeCreditoDTO extends MetodoDePagoDTO {
        public String titular;
        public String dniTitular;
        public String numeroTarj;
        public LocalDate caducidad;
        public String codigoCVV;
        public String correo;
        public Integer cuotas;
    }

    public static class TarjetaDeDebitoDTO extends MetodoDePagoDTO {
        public EmisorTarjeta emisor;
        public String titular;
        public String dniTitular;
        public String numeroTarj;
        public LocalDate caducidad;
        public String codigoCVV;
        public String correo;
    }

    public static class ChequesPropiosDTO extends MetodoDePagoDTO {
        public Integer numCheque;
        public String nombreHuesped;
        public String banco;
        public BigDecimal monto;
        public LocalDate fecha;
    }

    public static class ChequesDeTercerosDTO extends MetodoDePagoDTO {
        public Integer numCheque;
        public String emisor;
        public String banco;
        public LocalDate fecha;
    }
}
