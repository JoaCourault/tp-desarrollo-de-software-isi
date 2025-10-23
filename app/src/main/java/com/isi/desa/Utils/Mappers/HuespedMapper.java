package com.isi.desa.Utils.Mappers;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Dto.TipoDocumento.TipoDocumentoDTO;
import com.isi.desa.Model.Entities.Huesped.Huesped;

public class HuespedMapper {

    public static HuespedDTO entitytoDTO(Huesped h) {
        HuespedDTO dto = new HuespedDTO();

        // Tipo de documento (null-safe)
        if (h.getTipoDocumento() != null) {
            TipoDocumentoDTO tipoDocdto = new TipoDocumentoDTO();
            tipoDocdto.tipoDocumento = h.getTipoDocumento().getTipoDocumento();
            tipoDocdto.descripcion   = h.getTipoDocumento().getDescripcion();
            dto.tipoDocumento = tipoDocdto;
        } else {
            dto.tipoDocumento = null;
        }

        // Campos simples
        dto.idHuesped      = h.getIdHuesped();
        dto.nombre         = h.getNombre();
        dto.apellido       = h.getApellido();
        dto.numDoc         = h.getNumDoc();
        dto.cuit           = h.getCuit();
        dto.posicionIva    = h.getPosicionIva();
        dto.fechaNacimiento= h.getFechaNacimiento();
        dto.telefono       = h.getTelefono();
        dto.email          = h.getEmail();
        dto.ocupacion      = h.getOcupacion();
        dto.nacionalidad   = h.getNacionalidad();

        // Dirección (null-safe)
        dto.direccion = (h.getDireccion() != null)
                ? DireccionMapper.entityToDto(h.getDireccion())
                : null;

        return dto;
    }
}
