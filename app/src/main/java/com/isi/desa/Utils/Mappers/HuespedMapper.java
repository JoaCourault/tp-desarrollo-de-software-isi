package com.isi.desa.Utils.Mappers;

import com.isi.desa.Dao.Interfaces.ITipoDocumentoDAO;
import com.isi.desa.Dto.Direccion.DireccionDTO;
import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Dto.TipoDocumento.TipoDocumentoDTO;
import com.isi.desa.Model.Entities.Direccion.Direccion;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Model.Entities.Tipodocumento.TipoDocumento;
import org.springframework.stereotype.Component;

@Component
public class HuespedMapper {

    private final ITipoDocumentoDAO tipoDocumentoDAO;

    public HuespedMapper(ITipoDocumentoDAO tipoDocumentoDAO) {
        this.tipoDocumentoDAO = tipoDocumentoDAO;
    }

    public HuespedDTO entityToDTO(Huesped h) {
        if (h == null) return null;

        HuespedDTO dto = new HuespedDTO();
        dto.idHuesped = h.getIdHuesped();
        dto.nombre = h.getNombre();
        dto.apellido = h.getApellido();
        dto.numDoc = h.getNumDoc();
        dto.posicionIva = h.getPosicionIva();
        dto.cuit = h.getCuit();
        dto.fechaNacimiento = h.getFechaNac();
        dto.telefono = h.getTelefono();
        dto.email = h.getEmail();
        dto.ocupacion = h.getOcupacion();
        dto.nacionalidad = h.getNacionalidad();
        dto.eliminado = h.isEliminado();

        // Tipo documento
        TipoDocumento td = tipoDocumentoDAO.obtener(h.getTipoDocumento());
        TipoDocumentoDTO tdDTO = new TipoDocumentoDTO();
        tdDTO.tipoDocumento = td.getTipoDocumento();
        dto.tipoDocumento = tdDTO;

        // Dirección
        dto.direccion = DireccionMapper.entityToDto(h.getDireccion());

        return dto;
    }

    public Huesped dtoToEntity(HuespedDTO dto) {
        if (dto == null) return null;

        Huesped h = new Huesped();
        h.setIdHuesped(dto.idHuesped);
        h.setNombre(dto.nombre);
        h.setApellido(dto.apellido);
        h.setNumDoc(dto.numDoc);
        h.setPosicionIva(dto.posicionIva);
        h.setCuit(dto.cuit);
        h.setFechaNac(dto.fechaNacimiento);
        h.setTelefono(dto.telefono);
        h.setEmail(dto.email);
        h.setOcupacion(dto.ocupacion);
        h.setNacionalidad(dto.nacionalidad);
        h.setEliminado(dto.eliminado);

        if (dto.tipoDocumento != null) {
            TipoDocumento td = tipoDocumentoDAO.obtener(dto.tipoDocumento.tipoDocumento);
            h.setTipoDocumento(td.getTipoDocumento());
        }

        // Dirección
        Direccion direccion = DireccionMapper.dtoToEntity(dto.direccion);
        h.setDireccion(direccion);

        return h;
    }
}
