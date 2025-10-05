package com.isi.desa.Dto.MetodoDePago;

import com.isi.desa.Model.Enums.TipoMoneda;
import com.isi.desa.Model.Enums.EmisorTarjeta;

import java.time.LocalDateTime;

public class MetodoDePagoDTO {
    public TipoMoneda moneda;
    public Integer num_cheque;
    public String nombre_huesped;
    public String banco;
    public Float monto;
    public LocalDateTime fecha;
    public String emisor;
    public String titular;
    public Integer dniTitular;
    public Integer numero_tarj;
    public String caducidad;
    public Integer codigoCVV;
    public String correo;
    public Integer cuotas;
    public EmisorTarjeta tipo;
}
