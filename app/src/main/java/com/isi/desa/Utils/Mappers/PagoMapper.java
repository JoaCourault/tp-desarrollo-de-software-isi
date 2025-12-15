package com.isi.desa.Utils.Mappers;

import com.isi.desa.Dto.Pago.PagoDTO;
import com.isi.desa.Model.Entities.Pago.Pago;
import org.springframework.stereotype.Component;

@Component
public class PagoMapper {

    public PagoDTO entityToDto (Pago pago) {
        if (pago == null) return null;
        PagoDTO dto = new PagoDTO();
        dto.idPago = pago.getIdPago();
        dto.valor = pago.getValor();
        dto.fecha = pago.getFecha();

        return dto;
    }

    public Pago dtoToEntity (PagoDTO pagoDTO) {
        if (pagoDTO == null) return null;
        Pago pago = new Pago();
        // Generalmente el ID no se setea al crear la entidad (es autogenerado),
        // así que tu lógica de solo setear valor y fecha está bien.
        pago.setValor(pagoDTO.valor);
        pago.setFecha(pagoDTO.fecha);

        return pago;
    }
}