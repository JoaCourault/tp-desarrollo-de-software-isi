package com.isi.desa.Service.Implementations;

import com.isi.desa.Dao.Implementations.HuespedDAO;
import com.isi.desa.Dao.Implementations.DireccionDAO;
import com.isi.desa.Dao.Interfaces.IHuespedDAO;
import com.isi.desa.Dto.Direccion.DireccionDTO;
import com.isi.desa.Dto.Huesped.BuscarHuespedRequestDTO;
import com.isi.desa.Dto.Huesped.BuscarHuespedResultDTO;
import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Dto.Resultado;
import com.isi.desa.Model.Entities.Direccion.Direccion;
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
            // 1) Validación de alta
            Huesped entidadValidada = validator.create(huespedDTO);

            // 2) Persistir
            Huesped creado = dao.crear(huespedDTO);

            // 3) DTO resultante
            return HuespedMapper.entitytoDTO(creado);
        } catch (Exception e) {
            throw new RuntimeException("Error al crear huésped: " + e.getMessage(), e);
        }
    }

    @Override
    public HuespedDTO modificar(HuespedDTO dto) {
        try {
            // Validación "light" para update (solo lo que viene)
            List<String> errores = ((HuespedValidator) validator).validateUpdate(dto);
            if (errores != null && !errores.isEmpty()) {
                throw new IllegalArgumentException(String.join(", ", errores));
            }

            Huesped mod = dao.modificar(dto);
            return HuespedMapper.entitytoDTO(mod);
        } catch (Exception e) {
            throw new RuntimeException("Error al modificar huésped: " + e.getMessage(), e);
        }
    }

    public HuespedDTO modificar(HuespedDTO dto, boolean aceptarIgualmente) {
        try {
            List<String> errores = ((HuespedValidator) validator).validateUpdate(dto);
            if (errores != null && !errores.isEmpty()) {
                throw new IllegalArgumentException(String.join(", ", errores));
            }

            // Regla CU 2.B: chequeo de duplicado salvo "ACEPTAR IGUALMENTE"
            if (!aceptarIgualmente &&
                    dto.tipoDocumento != null &&
                    dto.numDoc != null &&
                    dao.existePorTipoYNumDocExceptoId(dto.tipoDocumento.tipoDocumento, dto.numDoc, dto.idHuesped)) {

                throw new IllegalStateException("¡CUIDADO! El tipo y número de documento ya existen en el sistema");
            }

            Huesped mod = dao.modificar(dto);
            return HuespedMapper.entitytoDTO(mod);
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

        // 1) Traer todas las entidades
        List<Huesped> entidades = dao.leerHuespedes();

        // 2) Filtrar por criterios si vienen
        if (requestDTO != null && requestDTO.huesped != null) {
            HuespedDTO filtros = requestDTO.huesped;
            entidades = entidades.stream()
                    .filter(h ->
                            (filtros.nombre == null || (h.getNombre() != null && h.getNombre().equalsIgnoreCase(filtros.nombre))) &&
                                    (filtros.apellido == null || (h.getApellido() != null && h.getApellido().equalsIgnoreCase(filtros.apellido))) &&
                                    (filtros.numDoc == null || (h.getNumDoc() != null && h.getNumDoc().equals(filtros.numDoc)))
                    )
                    .collect(Collectors.toList());
        }

        // 3) HIDRATAR dirección con datos completos si sólo viene el ID
        if (!entidades.isEmpty()) {
            DireccionDAO dirDao = new DireccionDAO();
            for (Huesped h : entidades) {
                // Determinar ID dirección
                String idDir = null;
                if (h.getDireccion() != null && h.getDireccion().getIdDireccion() != null) {
                    idDir = h.getDireccion().getIdDireccion();
                } else if (h.getIdDireccion() != null) {
                    idDir = h.getIdDireccion();
                }

                // ¿Está incompleta?
                boolean direccionIncompleta =
                        (h.getDireccion() == null) ||
                                (h.getDireccion().getCalle() == null) ||
                                (h.getDireccion().getPais() == null);

                // Completar desde DireccionDAO si corresponde
                if (idDir != null && direccionIncompleta) {
                    DireccionDTO q = new DireccionDTO();
                    q.id = idDir;
                    Direccion completa = dirDao.obtener(q);
                    if (completa != null) {
                        h.setDireccion(completa);
                    }
                }
            }
        }

        // 4) Mapear a DTO
        List<HuespedDTO> huespedesEncontrados = entidades.stream()
                .map(HuespedMapper::entitytoDTO)
                .collect(Collectors.toList());

        resultDTO.huespedesEncontrados = huespedesEncontrados;

        // 5) Armar resultado
        if (huespedesEncontrados.isEmpty()) {
            resultDTO.resultado.id = 2; // NoEncontrado
            resultDTO.resultado.mensaje = "No se encontraron huespedes con los filtros especificados.";
        } else {
            resultDTO.resultado.id = 0; // Éxito
            resultDTO.resultado.mensaje = "Busqueda exitosa.";
        }

        return resultDTO;
    }
}
