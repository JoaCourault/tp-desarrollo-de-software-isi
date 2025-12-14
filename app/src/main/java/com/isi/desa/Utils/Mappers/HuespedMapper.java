package com.isi.desa.Utils.Mappers;

import com.isi.desa.Dao.Implementations.TipoDocumentoDAO;
import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Dto.TipoDocumento.TipoDocumentoDTO;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Model.Entities.Tipodocumento.TipoDocumento;


public class HuespedMapper {
    public static HuespedDTO entityToDTO(Huesped h) {
        if (h == null) return null;
        TipoDocumentoDTO tipoDocDto = null;
        TipoDocumento tdEntity = h.getTipoDoc();
        if (tdEntity != null) {
            String id = tdEntity.getTipoDocumento();
            tipoDocDto = new TipoDocumentoDTO();
            tipoDocDto.tipoDocumento = id;
        }

        HuespedDTO dto = new HuespedDTO();
        dto.idHuesped = h.getIdHuesped();
        dto.nombre = h.getNombre();
        dto.apellido = h.getApellido();
        dto.numDoc = h.getNumDoc();
        dto.tipoDoc = tipoDocDto;
        dto.cuit = h.getCuit();
        dto.posicionIva = h.getPosicionIva();
        dto.fechaNac = h.getFechaNac();
        dto.telefono = h.getTelefono();
        dto.email = h.getEmail();
        dto.ocupacion = h.getOcupacion();
        dto.nacionalidad = h.getNacionalidad();
        dto.direccion = DireccionMapper.entityToDto(h.getDireccion());

        dto.eliminado = h.isEliminado();

        return dto;
    }

    public static Huesped dtoToEntity(HuespedDTO dto) {
        TipoDocumentoDAO tipoDocumentoDAO = new TipoDocumentoDAO();
        if (dto == null) return null;

        Huesped h = new Huesped();
        h.setIdHuesped(dto.idHuesped);
        h.setNombre(dto.nombre);
        h.setApellido(dto.apellido);
        h.setNumDoc(dto.numDoc);
        h.setPosicionIva(dto.posicionIva);
        h.setCuit(dto.cuit);
        h.setFechaNac(dto.fechaNac);
        h.setTelefono(dto.telefono);
        h.setEmail(dto.email);
        h.setOcupacion(dto.ocupacion);
        h.setNacionalidad(dto.nacionalidad);

        // TipoDocumento
        if (dto.tipoDoc != null) {
            String tipoDocId = dto.tipoDoc.tipoDocumento;
            h.setTipoDoc(tipoDocumentoDAO.obtener(tipoDocId));
        }

        h.setEliminado(dto.eliminado);

        return h;
    }
    public static java.util.List<Huesped> dtoListToEntitiesList(java.util.List<HuespedDTO> dtoList) {
        java.util.List<Huesped> huespedes = new java.util.ArrayList<>();
        for (HuespedDTO dto : dtoList) {
            huespedes.add(dtoToEntity(dto));
        }
        return huespedes;
    }
    public static java.util.List<HuespedDTO> entityListToDtoList(java.util.List<Huesped> huespedes) {
        java.util.List<HuespedDTO> dtoList = new java.util.ArrayList<>();
        for (Huesped h : huespedes) {
            dtoList.add(entityToDTO(h));
        }
        return dtoList;
    }
}
