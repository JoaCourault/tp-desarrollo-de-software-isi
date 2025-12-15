package com.isi.desa.Utils.Mappers;

import com.isi.desa.Dao.Interfaces.IHuespedDAO;
import com.isi.desa.Dao.Interfaces.ITipoDocumentoDAO;
import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Dto.TipoDocumento.TipoDocumentoDTO;
import com.isi.desa.Model.Entities.Direccion.Direccion;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Model.Entities.Tipodocumento.TipoDocumento;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class HuespedMapper {

    private static ITipoDocumentoDAO staticTipoDocumentoDAO;
    private static IHuespedDAO staticHuespedDAO;

    @Autowired private ITipoDocumentoDAO tipoDocumentoDAO;
    @Autowired @Lazy private IHuespedDAO huespedDAO;

    @PostConstruct
    public void init() {
        staticTipoDocumentoDAO = this.tipoDocumentoDAO;
        staticHuespedDAO = this.huespedDAO;
    }

    public static HuespedDTO entityToDTO(Huesped h) {
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
            TipoDocumento tdEntity = staticTipoDocumentoDAO.obtener(h.getTipoDocumento());
            if (tdEntity != null) {
                dto.tipoDocumento = new TipoDocumentoDTO();
                dto.tipoDocumento.tipoDocumento = tdEntity.getTipoDocumento();
            }
        }

        if (h.getDireccion() != null) {
            dto.direccion = DireccionMapper.entityToDto(h.getDireccion());
        }

        try {
            dto.estadias = EstadiaMapper.entityListToDtoList(staticHuespedDAO.obtenerEstadiasDeHuesped(h.getIdHuesped()));
        } catch (Exception e) {
            dto.estadias = Collections.emptyList();
        }

        return dto;
    }

    public static Huesped dtoToEntity(HuespedDTO dto) {
        if (dto == null) return null;

        Huesped h = new Huesped();
        h.setIdHuesped(dto.idHuesped); // El ID se setea en el DAO si es null
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

        if (dto.tipoDocumento != null && dto.tipoDocumento.tipoDocumento != null) {
            h.setTipoDocumento(dto.tipoDocumento.tipoDocumento);
        }

        if (dto.direccion != null) {
            Direccion dirEntity = DireccionMapper.dtoToEntity(dto.direccion);
            h.setDireccion(dirEntity);
        }

        return h;
    }
}