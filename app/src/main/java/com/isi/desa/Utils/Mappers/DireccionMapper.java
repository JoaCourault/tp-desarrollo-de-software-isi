package com.isi.desa.Utils.Mappers;

import com.isi.desa.Dto.Direccion.DireccionDTO;
import com.isi.desa.Model.Entities.Direccion.Direccion;
import org.springframework.stereotype.Component;

@Component
public class DireccionMapper {

    public static DireccionDTO entityToDto(Direccion direccionFinal) {
        if (direccionFinal == null) return null;
        DireccionMapper mapper = new DireccionMapper();
        return mapper.entityToDTO(direccionFinal);
    }

    public DireccionDTO entityToDTO(Direccion d) {
        if (d == null) return null;
        DireccionDTO dto = new DireccionDTO();
        dto.id = d.getIdDireccion();
        dto.pais = d.getPais();
        dto.provincia = d.getProvincia();
        dto.localidad = d.getLocalidad();
        dto.codigoPostal = d.getCp();
        dto.calle = d.getCalle();
        dto.numero = d.getNumero();
        dto.departamento = d.getDepartamento();

        // CORRECCIÓN AQUÍ: Validar si es null antes de convertir
        if (d.getPiso() != null) {
            dto.piso = String.valueOf(d.getPiso());
        } else {
            dto.piso = null;
        }

        return dto;
    }

    public Direccion dtoToEntity(DireccionDTO dto) {
        if (dto == null) return null;
        Direccion d = new Direccion();
        d.setIdDireccion(dto.id);
        d.setPais(dto.pais);
        d.setProvincia(dto.provincia);
        d.setLocalidad(dto.localidad);
        d.setCp(dto.codigoPostal);
        d.setCalle(dto.calle);
        d.setNumero(dto.numero);
        d.setDepartamento(dto.departamento);

        // CORRECCIÓN AQUÍ: Validar si viene texto antes de parsear a Integer
        if (dto.piso != null && !dto.piso.isBlank()) {
            try {
                d.setPiso(Integer.parseInt(dto.piso));
            } catch (NumberFormatException e) {
                // Si mandan "PB" o algo no numérico, lo dejamos en null o manejamos el error
                d.setPiso(null);
            }
        } else {
            d.setPiso(null);
        }

        return d;
    }
}