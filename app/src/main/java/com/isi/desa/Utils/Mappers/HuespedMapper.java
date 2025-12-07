package com.isi.desa.Utils.Mappers;

import com.isi.desa.Dao.Interfaces.IDireccionDAO;
import com.isi.desa.Dao.Interfaces.IHuespedDAO;
import com.isi.desa.Dao.Interfaces.ITipoDocumentoDAO;
import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Dto.TipoDocumento.TipoDocumentoDTO;
import com.isi.desa.Model.Entities.Direccion.Direccion;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy; // <--- IMPORTANTE
import org.springframework.stereotype.Component;

@Component
public class HuespedMapper {

    @Autowired
    private ITipoDocumentoDAO tipoDocumentoDAO;

    @Autowired
    private IDireccionDAO direccionDAO;

    @Autowired
    @Lazy // <--- ESTO ROMPE EL CICLO CIRCULAR
    private IHuespedDAO huespedDAO;

    public HuespedDTO entityToDTO(Huesped h) {
        if (h == null) return null;

        HuespedDTO dto = new HuespedDTO();
        dto.idHuesped = h.getIdHuesped();
        dto.nombre = h.getNombre();
        dto.apellido = h.getApellido();
        dto.numDoc = h.getNumDoc();
        dto.cuit = h.getCuit();
        dto.posicionIva = h.getPosicionIva();
        dto.fechaNacimiento = h.getFechaNac();
        dto.telefono = h.getTelefono();
        dto.email = h.getEmail();
        dto.ocupacion = h.getOcupacion();
        dto.nacionalidad = h.getNacionalidad();
        dto.eliminado = h.isEliminado();

        if (h.getTipoDocumento() != null) {
            TipoDocumentoDTO tipoDocDto = new TipoDocumentoDTO();
            tipoDocDto.tipoDocumento = h.getTipoDocumento();
            dto.tipoDocumento = tipoDocDto;
        }

        Direccion dEntity = direccionDAO.obtenerDireccionDeHuespedPorId(h.getIdHuesped());
        if (dEntity != null) {
            dto.direccion = DireccionMapper.entityToDto(dEntity);
        }

        try {
            dto.estadias = EstadiaMapper.entityListToDtoList(huespedDAO.obtenerEstadiasDeHuesped(h.getIdHuesped()));
        } catch (Exception e) {
            dto.estadias = new java.util.ArrayList<>();
        }

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
            h.setTipoDocumento(dto.tipoDocumento.tipoDocumento);
        }

        return h;
    }
}