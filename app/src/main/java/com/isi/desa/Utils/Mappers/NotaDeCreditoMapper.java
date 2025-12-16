package com.isi.desa.Utils.Mappers;

import com.isi.desa.Dto.NotaDeCredito.NotaDeCreditoDTO;
import com.isi.desa.Model.Entities.NotaDeCredito.NotaDeCredito;
import org.springframework.stereotype.Component;

@Component
public class NotaDeCreditoMapper {

    public NotaDeCredito dtoToEntity(NotaDeCreditoDTO dto) {
        if (dto == null) return null;
        NotaDeCredito entity = new NotaDeCredito();
        entity.setCodigoIdentificador(dto.codigoIdentificador);
        entity.setCobrado(dto.cobrado);
        // No mapeamos facturas para evitar recursividad
        entity.setFacturas(null);
        return entity;
    }

    public NotaDeCreditoDTO entityToDto(NotaDeCredito entity) {
        if (entity == null) return null;
        NotaDeCreditoDTO dto = new NotaDeCreditoDTO();
        dto.codigoIdentificador = entity.getCodigoIdentificador();
        dto.cobrado = entity.isCobrado();
        return dto;
    }
}