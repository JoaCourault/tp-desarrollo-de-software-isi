package com.isi.desa.Utils.Mappers;

import com.isi.desa.Dto.Direccion.DireccionDTO;
import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Dto.TipoDocumento.TipoDocumentoDTO;
import com.isi.desa.Model.Entities.Direccion.Direccion;
import com.isi.desa.Model.Entities.Huesped.Huesped;

import java.util.ArrayList;

public class HuespedMapper {

    public static HuespedDTO entityToDTO(Huesped h) {
        if (h == null) return null;

        TipoDocumentoDTO tipoDocDto = new TipoDocumentoDTO();
        if (h.getTipoDocumento() != null) {
            tipoDocDto.tipoDocumento = h.getTipoDocumento().getTipoDocumento();
            tipoDocDto.descripcion = h.getTipoDocumento().getDescripcion();
        }

        HuespedDTO dto = new HuespedDTO();
        dto.idHuesped = h.getIdHuesped();
        dto.nombre = h.getNombre();
        dto.apellido = h.getApellido();
        dto.numDoc = h.getNumDoc();
        dto.tipoDocumento = tipoDocDto;
        dto.cuit = h.getCuit();
        dto.posicionIva = h.getPosicionIva();
        dto.fechaNacimiento = h.getFechaNacimiento();
        dto.telefono = h.getTelefono();
        dto.email = h.getEmail();
        dto.ocupacion = h.getOcupacion();
        dto.nacionalidad = h.getNacionalidad();
        dto.direccion = DireccionMapper.entityToDto(h.getDireccion());
        dto.idsEstadias = (h.getIdsEstadias() != null)
                ? new ArrayList<>(h.getIdsEstadias())
                : new ArrayList<>();

        return dto;
    }

    public static Huesped dtoToEntity(HuespedDTO dto) {
        if (dto == null) return null;

        Huesped h = new Huesped();
        h.setIdHuesped(dto.idHuesped);
        h.setNombre(dto.nombre);
        h.setApellido(dto.apellido);
        h.setNumDoc(dto.numDoc);
        h.setPosicionIva(dto.posicionIva);
        h.setCuit(dto.cuit);
        h.setFechaNacimiento(dto.fechaNacimiento);
        h.setTelefono(dto.telefono);
        h.setEmail(dto.email);
        h.setOcupacion(dto.ocupacion);
        h.setNacionalidad(dto.nacionalidad);
        h.setDireccion(DireccionMapper.dtoToEntity(dto.direccion));

        //setear la lista de IDs de estadías
        h.setIdsEstadias(
                dto.idsEstadias != null ? new ArrayList<>(dto.idsEstadias) : new ArrayList<>()
        );

        return h;
    }

}