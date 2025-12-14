package com.isi.desa.Utils.Mappers;

import com.isi.desa.Dto.MetodoDePago.MetodoDePagoDTO;
import com.isi.desa.Model.Entities.MetodoDePago.*;

public class MetodoDePagoMapper {
    public static MetodoDePagoDTO entityToDto(MetodoDePago entity) {
        if (entity == null) return null;
        if (entity instanceof Efectivo efectivo) {
            MetodoDePagoDTO.EfectivoDTO dto = new MetodoDePagoDTO.EfectivoDTO();
            dto.idMetodoDePago = efectivo.getIdMetodoDePago();
            dto.divisa = efectivo.getDivisa();
            dto.tipoDeCambio = efectivo.getTipoDeCambio();
            return dto;
        } else if (entity instanceof TarjetaDeCredito tdc) {
            MetodoDePagoDTO.TarjetaDeCreditoDTO dto = new MetodoDePagoDTO.TarjetaDeCreditoDTO();
            dto.idMetodoDePago = tdc.getIdMetodoDePago();
            dto.titular = tdc.getTitular();
            dto.dniTitular = tdc.getDniTitular();
            dto.numeroTarj = tdc.getNumeroTarj();
            dto.caducidad = tdc.getCaducidad();
            dto.codigoCVV = tdc.getCodigoCVV();
            dto.correo = tdc.getCorreo();
            dto.cuotas = tdc.getCuotas();
            return dto;
        } else if (entity instanceof TarjetaDeDebito tdd) {
            MetodoDePagoDTO.TarjetaDeDebitoDTO dto = new MetodoDePagoDTO.TarjetaDeDebitoDTO();
            dto.idMetodoDePago = tdd.getIdMetodoDePago();
            dto.emisor = tdd.getEmisor();
            dto.titular = tdd.getTitular();
            dto.dniTitular = tdd.getDniTitular();
            dto.numeroTarj = tdd.getNumeroTarj();
            dto.caducidad = tdd.getCaducidad();
            dto.codigoCVV = tdd.getCodigoCVV();
            dto.correo = tdd.getCorreo();
            return dto;
        } else if (entity instanceof ChequesPropios cp) {
            MetodoDePagoDTO.ChequesPropiosDTO dto = new MetodoDePagoDTO.ChequesPropiosDTO();
            dto.idMetodoDePago = cp.getIdMetodoDePago();
            dto.numCheque = cp.getNumCheque();
            dto.nombreHuesped = cp.getNombreHuesped();
            dto.banco = cp.getBanco();
            dto.monto = cp.getMonto();
            dto.fecha = cp.getFecha();
            return dto;
        } else if (entity instanceof ChequesDeTerceros ct) {
            MetodoDePagoDTO.ChequesDeTercerosDTO dto = new MetodoDePagoDTO.ChequesDeTercerosDTO();
            dto.idMetodoDePago = ct.getIdMetodoDePago();
            dto.numCheque = ct.getNumCheque();
            dto.emisor = ct.getEmisor();
            dto.banco = ct.getBanco();
            dto.fecha = ct.getFecha();
            return dto;
        }
        return null;
    }

    public static MetodoDePago dtoToEntity(MetodoDePagoDTO dto) {
        if (dto == null) return null;
        if (dto instanceof MetodoDePagoDTO.EfectivoDTO efectivoDto) {
            Efectivo entity = new Efectivo();
            entity.setIdMetodoDePago(efectivoDto.idMetodoDePago);
            entity.setDivisa(efectivoDto.divisa);
            entity.setTipoDeCambio(efectivoDto.tipoDeCambio);
            return entity;
        } else if (dto instanceof MetodoDePagoDTO.TarjetaDeCreditoDTO tdcDto) {
            TarjetaDeCredito entity = new TarjetaDeCredito();
            entity.setIdMetodoDePago(tdcDto.idMetodoDePago);
            entity.setTitular(tdcDto.titular);
            entity.setDniTitular(tdcDto.dniTitular);
            entity.setNumeroTarj(tdcDto.numeroTarj);
            entity.setCaducidad(tdcDto.caducidad);
            entity.setCodigoCVV(tdcDto.codigoCVV);
            entity.setCorreo(tdcDto.correo);
            entity.setCuotas(tdcDto.cuotas);
            return entity;
        } else if (dto instanceof MetodoDePagoDTO.TarjetaDeDebitoDTO tddDto) {
            TarjetaDeDebito entity = new TarjetaDeDebito();
            entity.setIdMetodoDePago(tddDto.idMetodoDePago);
            entity.setEmisor(tddDto.emisor);
            entity.setTitular(tddDto.titular);
            entity.setDniTitular(tddDto.dniTitular);
            entity.setNumeroTarj(tddDto.numeroTarj);
            entity.setCaducidad(tddDto.caducidad);
            entity.setCodigoCVV(tddDto.codigoCVV);
            entity.setCorreo(tddDto.correo);
            return entity;
        } else if (dto instanceof MetodoDePagoDTO.ChequesPropiosDTO cpDto) {
            ChequesPropios entity = new ChequesPropios();
            entity.setIdMetodoDePago(cpDto.idMetodoDePago);
            entity.setNumCheque(cpDto.numCheque);
            entity.setNombreHuesped(cpDto.nombreHuesped);
            entity.setBanco(cpDto.banco);
            entity.setMonto(cpDto.monto);
            entity.setFecha(cpDto.fecha);
            return entity;
        } else if (dto instanceof MetodoDePagoDTO.ChequesDeTercerosDTO ctDto) {
            ChequesDeTerceros entity = new ChequesDeTerceros();
            entity.setIdMetodoDePago(ctDto.idMetodoDePago);
            entity.setNumCheque(ctDto.numCheque);
            entity.setEmisor(ctDto.emisor);
            entity.setBanco(ctDto.banco);
            entity.setFecha(ctDto.fecha);
            return entity;
        }
        return null;
    }
}

