package com.isi.desa.Utils.Mappers;

import com.isi.desa.Dao.Interfaces.IDireccionDAO;
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

@Component // 1. Convertimos la clase en un Bean de Spring
public class HuespedMapper {

    // Variables estáticas para uso interno en métodos estáticos
    private static ITipoDocumentoDAO staticTipoDocumentoDAO;
    private static IDireccionDAO staticDireccionDAO;
    private static IHuespedDAO staticHuespedDAO;

    // 2. Inyectamos las dependencias en variables de instancia normales
    @Autowired
    private ITipoDocumentoDAO tipoDocumentoDAO;

    @Autowired
    private IDireccionDAO direccionDAO;

    @Autowired
    @Lazy // Usamos Lazy para evitar referencia circular (HuespedDAO -> Mapper -> HuespedDAO)
    private IHuespedDAO huespedDAO;

    // 3. Este método se ejecuta al iniciar Spring y copia los beans a las variables estáticas
    @PostConstruct
    public void init() {
        staticTipoDocumentoDAO = this.tipoDocumentoDAO;
        staticDireccionDAO = this.direccionDAO;
        staticHuespedDAO = this.huespedDAO;
    }

    // --- MÉTODOS ESTÁTICOS (Tu lógica original, usando las variables "static...") ---

    public static HuespedDTO entityToDTO(Huesped h) {
        if (h == null) return null;

        TipoDocumentoDTO tipoDocDto = null;
        // Usamos la variable estática ya inicializada
        if (h.getTipoDocumento() != null) {
            TipoDocumento tdEntity = staticTipoDocumentoDAO.obtener(h.getTipoDocumento());
            if (tdEntity != null) {
                tipoDocDto = new TipoDocumentoDTO();
                tipoDocDto.tipoDocumento = tdEntity.getTipoDocumento();
            }
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
        dto.eliminado = h.isEliminado();

        // Direccion
        // Nota: Asegúrate de que IDireccionDAO tenga este método implementado.
        // Si no lo tiene, tendrás que ajustarlo o manejar el null.
        try {
            Direccion dEntity = staticDireccionDAO.obtenerDireccionDeHuespedPorId(h.getIdHuesped());
            if (dEntity != null) {
                // Lógica de recarga si falta la calle (Opcional, la simplifiqué para seguridad)
                dto.direccion = DireccionMapper.entityToDto(dEntity);
            } else {
                dto.direccion = null;
            }
        } catch (Exception e) {
            dto.direccion = null;
        }

        // Estadias
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

        // TipoDocumento
        if (dto.tipoDocumento != null && dto.tipoDocumento.tipoDocumento != null) {
            String tipoDocId = dto.tipoDocumento.tipoDocumento;
            // Validamos que exista antes de asignarlo, o asignamos el ID directo
            TipoDocumento td = staticTipoDocumentoDAO.obtener(tipoDocId);
            if (td != null) {
                h.setTipoDocumento(td.getTipoDocumento());
            } else {
                // Fallback: Si no lo encuentra, asignamos el string directo (si tu lógica lo permite)
                // O lanzamos excepción si es estricto. Por ahora asigno el ID.
                h.setTipoDocumento(tipoDocId);
            }
        }

        return h;
    }
}