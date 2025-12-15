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
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class HuespedMapper {
    @Autowired
    private static ITipoDocumentoDAO tipoDocumentoDAO;

    public void setTipoDocumentoDAO(ITipoDocumentoDAO dao) {
        HuespedMapper.tipoDocumentoDAO = dao;
    }
    @Autowired
    private static IDireccionDAO direccionDAO;

    @Autowired
    @Lazy
    private IHuespedDAO huespedDAO;



    public static Huesped dtoToEntity(HuespedDTO dto) {
        if (dto == null) return null;

        // Tipo Documento
        TipoDocumento tipoDoc = tipoDocumentoDAO.obtener(dto.tipoDoc.tipoDocumento);
        if (tipoDoc == null) {
            throw new RuntimeException("El tipo de documento no existe");
        }

        // Dirección
        Direccion direccion = null;
        if (dto.direccion != null) {
            // Si tiene ID, BUSCAMOS la entidad gestionada en la BD
            if (dto.direccion.id != null && !dto.direccion.id.isEmpty()) {
                direccion = direccionDAO.getById(dto.direccion.id);

                // Si direccion es NULL aquí, significa que falló el guardado anterior.
                // NO creamos una nueva con ese ID, porque Hibernate fallará.
                if (direccion == null) {
                    throw new RuntimeException("Error interno: La dirección con ID " + dto.direccion.id + " no se encontró en la BD.");
                }
            } else {
                // Solo si NO tiene ID creamos una nueva (caso raro en update)
                direccion = DireccionMapper.dtoToEntity(dto.direccion);
            }
        }

        return new Huesped(
                dto.nombre,
                dto.apellido,
                tipoDoc,
                dto.numDoc,
                dto.posicionIva,
                dto.cuit,
                dto.fechaNac,
                dto.telefono,
                dto.email,
                dto.ocupacion,
                dto.nacionalidad,
                direccion,
                dto.idHuesped
        );
    }

    public static HuespedDTO entityToDTO(Huesped entity) {
        if (entity == null) return null;

        HuespedDTO dto = new HuespedDTO();
        dto.idHuesped = entity.getIdHuesped();
        dto.nombre = entity.getNombre();
        dto.apellido = entity.getApellido();
        dto.numDoc = entity.getNumDoc();
        dto.posicionIva = entity.getPosicionIva();
        dto.cuit = entity.getCuit();
        dto.fechaNac = entity.getFechaNac();
        dto.telefono = entity.getTelefono();
        dto.email = entity.getEmail();
        dto.ocupacion = entity.getOcupacion();
        dto.nacionalidad = entity.getNacionalidad();
        dto.eliminado = entity.isEliminado();

        if (entity.getTipoDoc() != null) {
            com.isi.desa.Dto.TipoDocumento.TipoDocumentoDTO tdDto = new com.isi.desa.Dto.TipoDocumento.TipoDocumentoDTO();
            tdDto.tipoDocumento = entity.getTipoDoc().getTipoDocumento();
            dto.tipoDoc = tdDto;
        }

        if (entity.getDireccion() != null) {

            dto.direccion = DireccionMapper.entityToDTO(entity.getDireccion());
        }

        return dto;
    }
}