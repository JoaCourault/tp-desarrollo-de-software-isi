package com.isi.desa.Utils.Mappers;

import com.isi.desa.Dao.Interfaces.IDireccionDAO;
import com.isi.desa.Dao.Interfaces.IHuespedDAO;
import com.isi.desa.Dao.Interfaces.ITipoDocumentoDAO;
import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Dto.TipoDocumento.TipoDocumentoDTO;
import com.isi.desa.Model.Entities.Direccion.Direccion;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Model.Entities.Tipodocumento.TipoDocumento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class HuespedMapper {

    @Autowired
    private ITipoDocumentoDAO tipoDocumentoDAO;

    @Autowired
    private IDireccionDAO direccionDAO;

    @Autowired
    @Lazy
    private IHuespedDAO huespedDAO;

    @Autowired
    private DireccionMapper direccionMapper;

    public Huesped dtoToEntity(HuespedDTO dto) {
        if (dto == null) return null;

        if (dto.tipoDoc == null || dto.tipoDoc.tipoDocumento == null) {
            throw new RuntimeException("El DTO no contiene el tipo de documento.");
        }

        TipoDocumento tipoDoc = tipoDocumentoDAO.obtener(dto.tipoDoc.tipoDocumento);
        if (tipoDoc == null) {
            throw new RuntimeException("El tipo de documento no existe en la BD.");
        }

        Direccion direccion = null;
        if (dto.direccion != null) {
            if (dto.direccion.id != null && !dto.direccion.id.isEmpty()) {
                direccion = direccionDAO.getById(dto.direccion.id);
                if (direccion == null) {
                    throw new RuntimeException("La dirección con ID " + dto.direccion.id + " no existe.");
                }
            } else {
                direccion = direccionMapper.dtoToEntity(dto.direccion);
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

    public HuespedDTO entityToDTO(Huesped entity) {
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
            TipoDocumentoDTO tdDto = new TipoDocumentoDTO();
            tdDto.tipoDocumento = entity.getTipoDoc().getTipoDocumento();
            dto.tipoDoc = tdDto;
        }

        if (entity.getDireccion() != null) {
            // 2. USAMOS LA INSTANCIA INYECTADA
            dto.direccion = direccionMapper.entityToDTO(entity.getDireccion());
        }

        return dto;
    }
}
