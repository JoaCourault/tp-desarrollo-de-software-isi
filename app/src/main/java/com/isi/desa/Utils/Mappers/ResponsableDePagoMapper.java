package com.isi.desa.Utils.Mappers;

import com.isi.desa.Dto.ResponsableDePago.ResponsableDePagoDTO;
import com.isi.desa.Model.Entities.ResponsableDePago.ResponsableDePago;
import com.isi.desa.Model.Entities.ResponsableDePago.PersonaFisica;
import com.isi.desa.Model.Entities.ResponsableDePago.PersonaJuridica;
import com.isi.desa.Dto.ResponsableDePago.PersonaFisica.PersonaFisicaDTO;
import com.isi.desa.Dto.ResponsableDePago.PersonaJuridica.PersonaJuridicaDTO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


public class ResponsableDePagoMapper {
    @Autowired
    private HuespedMapper HuespedMapper;

    public static ResponsableDePagoDTO entityToDto (ResponsableDePago responsableDePago) {

        if (responsableDePago == null) return null;
        ResponsableDePagoDTO dto;
        if (responsableDePago instanceof PersonaFisica pf) {
            PersonaFisicaDTO pfDto = new PersonaFisicaDTO();
            pfDto.huesped = com.isi.desa.Utils.Mappers.HuespedMapper.entityToDTO(pf.getHuesped());
            pfDto.idResponsableDePago = pf.getIdResponsableDePago();
            dto = pfDto;
        } else if (responsableDePago instanceof PersonaJuridica pj) {
            PersonaJuridicaDTO pjDto = new PersonaJuridicaDTO();
            pjDto.razonSocial = pj.getRazonSocial();
            pjDto.telefono = pj.getTelefono();
            pjDto.direccion = DireccionMapper.entityToDto(pj.getDireccion());
            pjDto.cuit = pj.getCuit();
            pjDto.idResponsableDePago = pj.getIdResponsableDePago();
            dto = pjDto;
        } else {
            return null;
        }
        dto.idResponsableDePago = responsableDePago.getIdResponsableDePago();
        return dto;
    }

    public static ResponsableDePago dtoToEntity (ResponsableDePagoDTO responsableDePagoDTO) {

        if (responsableDePagoDTO == null) return null;
        if (responsableDePagoDTO instanceof PersonaFisicaDTO pfDto) {
            PersonaFisica pf = new PersonaFisica();
            pf.setHuesped(com.isi.desa.Utils.Mappers.HuespedMapper.dtoToEntity(pfDto.huesped));
            pf.setIdResponsableDePago(pfDto.idResponsableDePago);
            return pf;
        } else if (responsableDePagoDTO instanceof PersonaJuridicaDTO pjDto) {
            PersonaJuridica pj = new PersonaJuridica();
            pj.setRazonSocial(pjDto.razonSocial);
            pj.setTelefono(pjDto.telefono);
            pj.setDireccion(DireccionMapper.dtoToEntity(pjDto.direccion));
            pj.setCuit(pjDto.cuit);
            pj.setIdResponsableDePago(pjDto.idResponsableDePago);
            return pj;
        }
        return null;
    }

    public static List<ResponsableDePago> dtosToEntities (List<ResponsableDePagoDTO> dtos) {
        return dtos.stream().map(ResponsableDePagoMapper::dtoToEntity).toList();
    }

    public static List<ResponsableDePagoDTO> entitiesToDtos (List<ResponsableDePago> entities) {
        return entities.stream().map(ResponsableDePagoMapper::entityToDto).toList();
    }
}
