package com.isi.desa.Service.Implementations;

import com.isi.desa.Dao.Implementations.HuespedDAO;
import com.isi.desa.Dao.Interfaces.IHuespedDAO;
import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Dto.TipoDocumento.TipoDocumentoDTO;
import com.isi.desa.Model.Entities.Direccion.Direccion;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Service.Interfaces.IHuesped;
import com.isi.desa.Service.Implementations.Validators.HuespedValidator;
import com.isi.desa.Dao.Interfaces.IDireccionDAO;
import com.isi.desa.Utils.Mappers.DireccionMapper;
import com.isi.desa.Utils.Mappers.HuespedMapper;

import java.util.List;
import java.util.stream.Collectors;

import com.isi.desa.Utils.Mappers.HuespedMapper;

//@Service //Descomentar para Spring Boot
public class HuespedService implements IHuesped {

    private final HuespedValidator validator;
    private final IHuespedDAO dao;

    // Constructor con inyección del DAO existente
    public HuespedService(IHuespedDAO dao) {
        this.dao = dao;
        this.validator = new HuespedValidator();
    }

    @Override
    public HuespedDTO crear(HuespedDTO huespedDTO) {
        try {
            // 1. Validar y convertir a Entidad (si es necesario para la validación)
            // Asumo que el validator lanza excepción si hay error
            Huesped entidadValidada = validator.create(huespedDTO);

            // 2. DAO devuelve la Entidad creada
            Huesped creado = dao.crear(huespedDTO);

            // 3. Convertir a DTO para devolver
            return HuespedMapper.entitytoDTO(creado);
        } catch (Exception e) {
            // Aquí deberías loguear o lanzar una excepción más específica.
            throw new RuntimeException("Error al crear huésped: " + e.getMessage(), e);
        }
       /* try {
            validator.create(huespedDTO);           // Validación
            Huesped creado = dao.crear(huespedDTO); // DAO devuelve entidad
            return Optional.of(toDTO(creado));      // Convertir a DTO
        } catch (Exception e) {
            return Optional.empty();
        }*/
    }

    @Override
    public HuespedDTO modificar(HuespedDTO huespedDTO) {
        try {
            validator.create(huespedDTO);
            Huesped modificado = dao.modificar(huespedDTO);
            return HuespedMapper.entitytoDTO(modificado);
        } catch (Exception e) {
            throw new RuntimeException("Error al modificar huésped: " + e.getMessage(), e);
        }
    }

    @Override
    public HuespedDTO eliminar(HuespedDTO huespedDTO) {
        try {
            Huesped eliminado = dao.eliminar(huespedDTO);
            return HuespedMapper.entitytoDTO(eliminado);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar huésped: " + e.getMessage(), e);
        }
    }

    @Override
    public List<HuespedDTO> buscarHuesped(HuespedDTO filtros) {
        return dao.leerHuespedes().stream()
                .filter(h -> (filtros.nombre == null || h.getNombre().equalsIgnoreCase(filtros.nombre)) &&
                        (filtros.apellido == null || h.getApellido().equalsIgnoreCase(filtros.apellido)) &&
                        (filtros.numDoc == null || h.getNumDoc().equals(filtros.numDoc)))
                .map(HuespedMapper::entitytoDTO)
                .collect(Collectors.toList());
    }


}
