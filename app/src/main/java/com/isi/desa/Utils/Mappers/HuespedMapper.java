package com.isi.desa.Utils.Mappers;

import com.isi.desa.Dao.Implementations.TipoDocumentoDAO;
import com.isi.desa.Dao.Interfaces.IDireccionDAO;
import com.isi.desa.Dao.Interfaces.IHuespedDAO;
import com.isi.desa.Dao.Interfaces.ITipoDocumentoDAO;
import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Dto.TipoDocumento.TipoDocumentoDTO;
import com.isi.desa.Model.Entities.Direccion.Direccion;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Model.Entities.Tipodocumento.TipoDocumento;
import com.isi.desa.Dao.Implementations.DireccionDAO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

public class HuespedMapper {

    @Autowired
    public static ITipoDocumentoDAO tipoDocumentoDAO;

    @Autowired
    public static IDireccionDAO direccionDAO;

    @Autowired
    public static IHuespedDAO huespedDAO;

    public static HuespedDTO entityToDTO(Huesped h) {
        if (h == null) return null;

        TipoDocumentoDTO tipoDocDto = null;
        TipoDocumento tdEntity = tipoDocumentoDAO.obtener(h.getTipoDoc().getTipoDocumento());
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
        dto.tipoDocumento = tipoDocDto;
        dto.cuit = h.getCuit();
        dto.posicionIva = h.getPosicionIva();
        dto.fechaNacimiento = h.getFechaNac();
        dto.telefono = h.getTelefono();
        dto.email = h.getEmail();
        dto.ocupacion = h.getOcupacion();
        dto.nacionalidad = h.getNacionalidad();

        // Direccion: si viene solo con id, intentar completar desde DAO
        Direccion dEntity = direccionDAO.obtenerDireccionDeHuespedPorId(h.getIdHuesped());
        if (dEntity != null) {
            if ((dEntity.getCalle() == null || dEntity.getCalle().trim().isEmpty())
                    && dEntity.getIdDireccion() != null && !dEntity.getIdDireccion().trim().isEmpty()) {
                try {
                    com.isi.desa.Dto.Direccion.DireccionDTO req = new com.isi.desa.Dto.Direccion.DireccionDTO();
                    req.id = dEntity.getIdDireccion();
                    DireccionDAO direccionDAO = new DireccionDAO();
                    dEntity = direccionDAO.obtener(req);
                } catch (Exception ignore) {
                    // si falla, dejamos la direccion tal como viene (solo id)
                }
            }
            dto.direccion = DireccionMapper.entityToDto(dEntity);
        } else {
            dto.direccion = null;
        }

        dto.estadias = EstadiaMapper.entityListToDtoList(huespedDAO.obtenerEstadiasDeHuesped(h.getIdHuesped()));

        dto.eliminado = h.isEliminado();

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
        h.setFechaNac(dto.fechaNacimiento);
        h.setTelefono(dto.telefono);
        h.setEmail(dto.email);
        h.setOcupacion(dto.ocupacion);
        h.setNacionalidad(dto.nacionalidad);

        // TipoDocumento
        if (dto.tipoDocumento != null) {
            String tipoDocId = dto.tipoDocumento.tipoDocumento;
            h.setTipoDoc(tipoDocumentoDAO.obtener(tipoDocId));
        }

        h.setEliminado(dto.eliminado);

        return h;
    }
}
