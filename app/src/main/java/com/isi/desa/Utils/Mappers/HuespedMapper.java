package com.isi.desa.Utils.Mappers;

import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Dto.TipoDocumento.TipoDocumentoDTO;
import com.isi.desa.Model.Entities.Huesped.Huesped;

public class HuespedMapper {
    public static HuespedDTO entitytoDTO(Huesped h) {
        TipoDocumentoDTO tipoDocdto = new TipoDocumentoDTO();
        if (h.getTipoDocumento() != null) {
            tipoDocdto.tipoDocumento = h.getTipoDocumento().getTipoDocumento();
            tipoDocdto.descripcion = h.getTipoDocumento().getDescripcion();
        }

        HuespedDTO dto = new HuespedDTO();
        dto.idHuesped = h.getIdHuesped();
        dto.nombre = h.getNombre();
        dto.apellido = h.getApellido();
        dto.numDoc = h.getNumDoc();
        dto.tipoDocumento = tipoDocdto;
        dto.cuit = h.getCuit();
        dto.posicionIva = h.getPosicionIva();
        dto.fechaNacimiento = h.getFechaNacimiento();
        dto.telefono = h.getTelefono();
        dto.email = h.getEmail();
        dto.ocupacion = h.getOcupacion();
        dto.nacionalidad = h.getNacionalidad();
        dto.direccion = DireccionMapper.entityToDto(h.getDireccion());
        return dto;
    }
}
