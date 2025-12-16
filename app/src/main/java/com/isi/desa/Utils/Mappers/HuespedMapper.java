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
    // Usamos Lazy para evitar referencias circulares si el DAO usa el Mapper
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
        // CORRECCIÓN: Entidad (fechaNac) -> DTO (fechaNacimiento)
        dto.fechaNacimiento = h.getFechaNac();
        dto.telefono = h.getTelefono();
        dto.email = h.getEmail();
        dto.ocupacion = h.getOcupacion();
        dto.nacionalidad = h.getNacionalidad();
        dto.eliminado = h.isEliminado();

        // CORRECCIÓN: Extraemos el String del objeto TipoDocumento
        if (h.getTipoDocumento() != null) {
            dto.tipoDocumento = new TipoDocumentoDTO();
            dto.tipoDocumento.tipoDocumento = h.getTipoDocumento().getTipoDocumento();
        }

        if (h.getDireccion() != null) {
            dto.direccion = DireccionMapper.entityToDto(h.getDireccion());
        }

        try {
            // Nota: Esto asume que obtenerEstadiasDeHuesped funciona bien
            dto.estadias = EstadiaMapper.entityListToDtoList(staticHuespedDAO.obtenerEstadiasDeHuesped(h.getIdHuesped()));
        } catch (Exception e) {
            dto.estadias = Collections.emptyList();
        }

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
        // CORRECCIÓN: DTO (fechaNacimiento) -> Entidad (fechaNac)
        h.setFechaNac(dto.fechaNacimiento);
        h.setTelefono(dto.telefono);
        h.setEmail(dto.email);
        h.setOcupacion(dto.ocupacion);
        h.setNacionalidad(dto.nacionalidad);
        h.setEliminado(dto.eliminado);

        // CORRECCIÓN IMPORTANTE: Buscamos la entidad real en la BBDD
        if (dto.tipoDocumento != null && dto.tipoDocumento.tipoDocumento != null) {
            // Usamos el DAO estático para recuperar el objeto gestionado
            TipoDocumento td = staticTipoDocumentoDAO.obtener(dto.tipoDocumento.tipoDocumento);
            h.setTipoDocumento(td);
        }

        if (dto.direccion != null) {
            Direccion dirEntity = DireccionMapper.dtoToEntity(dto.direccion);
            // El setter del helper en Huesped vincula la relación bidireccional
            h.setDireccion(dirEntity);
        }

        return h;
    }
}