package com.isi.desa.Utils.Mappers;

import com.isi.desa.Dto.ResponsableDePago.ResponsableDePagoDTO;
import com.isi.desa.Model.Entities.ResponsableDePago.ResponsableDePago;
import com.isi.desa.Model.Entities.ResponsableDePago.PersonaFisica;
import com.isi.desa.Model.Entities.ResponsableDePago.PersonaJuridica;
import com.isi.desa.Dto.ResponsableDePago.PersonaFisica.PersonaFisicaDTO;
import com.isi.desa.Dto.ResponsableDePago.PersonaJuridica.PersonaJuridicaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ResponsableDePagoMapper {
    @Autowired
    private HuespedMapper HuespedMapper;


    @Autowired
    private HuespedMapper huespedMapper;

    @Autowired
    private DireccionMapper direccionMapper;

    public ResponsableDePagoDTO entityToDto (ResponsableDePago responsableDePago) {
        if (responsableDePago == null) return null;
        ResponsableDePagoDTO dto;
        if (responsableDePago instanceof PersonaFisica pf) {
            PersonaFisicaDTO pfDto = new PersonaFisicaDTO();

            pfDto.huesped = huespedMapper.entityToDTO(pf.getHuesped());
            pfDto.idResponsableDePago = pf.getIdResponsableDePago();
            dto = pfDto;
        } else if (responsableDePago instanceof PersonaJuridica pj) {
            PersonaJuridicaDTO pjDto = new PersonaJuridicaDTO();
            pjDto.razonSocial = pj.getRazonSocial();
            pjDto.telefono = pj.getTelefono();
            pjDto.direccion = direccionMapper.entityToDTO(pj.getDireccion());
            pjDto.cuit = pj.getCuit();
            pjDto.idResponsableDePago = pj.getIdResponsableDePago();
            dto = pjDto;
        } else {
            return null;
        }
        dto.idResponsableDePago = responsableDePago.getIdResponsableDePago();
        return dto;
    }


    public ResponsableDePago dtoToEntity (ResponsableDePagoDTO responsableDePagoDTO) {
        if (responsableDePagoDTO == null) return null;
        if (responsableDePagoDTO instanceof PersonaFisicaDTO pfDto) {
            PersonaFisica pf = new PersonaFisica();
            pf.setHuesped(huespedMapper.dtoToEntity(pfDto.huesped));
            pf.setIdResponsableDePago(pfDto.idResponsableDePago);
            return pf;
        } else if (responsableDePagoDTO instanceof PersonaJuridicaDTO pjDto) {
            PersonaJuridica pj = new PersonaJuridica();
            pj.setRazonSocial(pjDto.razonSocial);
            pj.setTelefono(pjDto.telefono);
            pj.setDireccion(direccionMapper.dtoToEntity(pjDto.direccion));
            pj.setCuit(pjDto.cuit);
            pj.setIdResponsableDePago(pjDto.idResponsableDePago);
            return pj;
        }
        return null;
    }

    public List<ResponsableDePago> dtosToEntities (List<ResponsableDePagoDTO> dtos) {
        return dtos.stream().map(this::dtoToEntity).toList();
    }

    public List<ResponsableDePagoDTO> entitiesToDtos (List<ResponsableDePago> entities) {
        return entities.stream().map(this::entityToDto).toList();
    }
}