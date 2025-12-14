package com.isi.desa.Utils.Mappers;

import com.isi.desa.Dto.Pago.PagoDTO;
import com.isi.desa.Model.Entities.Pago.Pago;


public class PagoMapper {
    public static PagoDTO entityToDto (Pago pago) {
        if (pago == null) return null;
        PagoDTO dto = new PagoDTO();
        dto.idPago = pago.getIdPago();
        dto.valor = pago.getValor();
        dto.fecha = pago.getFecha();

        return dto;
    }

    public static Pago dtoToEntity (PagoDTO pagoDTO) {
        if (pagoDTO == null) return null;
        Pago pago = new Pago();
        pago.setValor(pagoDTO.valor);
        pago.setFecha(pagoDTO.fecha);

        return pago;
    }
}
