package com.isi.desa.Service.Implementations;

import com.isi.desa.Dao.Implementations.HuespedDAO;
import com.isi.desa.Dao.Interfaces.IHuespedDAO;
import com.isi.desa.Dto.Huesped.BuscarHuespedRequestDTO;
import com.isi.desa.Dto.Huesped.BuscarHuespedResultDTO;
import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Dto.Resultado;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Service.Interfaces.IHuespedService;
import com.isi.desa.Service.Implementations.Validators.HuespedValidator;
import com.isi.desa.Service.Interfaces.Validators.IHuespedValidator;
import com.isi.desa.Utils.Mappers.HuespedMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//@Service //Descomentar para Spring Boot
public class HuespedService implements IHuespedService {
    //@Autowired //Descomentar para Spring Boot
    private final IHuespedValidator validator;
    //@Autowired //Descomentar para Spring Boot
    private final IHuespedDAO dao;

    // Constructor para inyección de dependencias manual (sin Spring Boot, borrar cuando se use Spring)
    public HuespedService() {
        this.dao = new HuespedDAO();
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
    public BuscarHuespedResultDTO buscarHuesped(BuscarHuespedRequestDTO requestDTO) {
        BuscarHuespedResultDTO resultDTO = new BuscarHuespedResultDTO();
        resultDTO.resultado = new Resultado();
        resultDTO.huespedesEncontrados = new ArrayList<>();

        List<HuespedDTO> huespedesEncontrados;

        if (requestDTO == null || requestDTO.huesped == null) {
            // Si no hay filtros, devolver todos los huéspedes
            huespedesEncontrados = dao.leerHuespedes().stream()
                    .map(HuespedMapper::entitytoDTO)
                    .collect(Collectors.toList());
        } else {
            HuespedDTO filtros = requestDTO.huesped;

            huespedesEncontrados = dao.leerHuespedes().stream()
                    .filter(h -> (filtros.nombre == null || h.getNombre().equalsIgnoreCase(filtros.nombre)) &&
                            (filtros.apellido == null || h.getApellido().equalsIgnoreCase(filtros.apellido)) &&
                            (filtros.numDoc == null || h.getNumDoc().equals(filtros.numDoc)))
                    .map(HuespedMapper::entitytoDTO)
                    .collect(Collectors.toList());
        }

        resultDTO.huespedesEncontrados = huespedesEncontrados;

        if (huespedesEncontrados.isEmpty()) {
            resultDTO.resultado.id = 2; // NoEncontrado (404)
            resultDTO.resultado.mensaje = "No se encontraron huespedes con los filtros especificados.";
        } else {
            resultDTO.resultado.id = 0; // Exito (200)
            resultDTO.resultado.mensaje = "Busqueda exitosa.";
        }

        return resultDTO;
    }
}
