package com.isi.desa.Service.Implementations;

import com.isi.desa.Dao.Implementations.DireccionDAO;
import com.isi.desa.Dao.Implementations.HuespedDAO;
import com.isi.desa.Dao.Interfaces.IHuespedDAO;
import com.isi.desa.Dto.Huesped.*;
import com.isi.desa.Dto.Resultado;
import com.isi.desa.Exceptions.Huesped.CannotCreateHuespedException;
import com.isi.desa.Exceptions.Huesped.CannotModifyHuespedEsception;
import com.isi.desa.Exceptions.Huesped.HuespedDuplicadoException;
import com.isi.desa.Exceptions.Huesped.HuespedNotFoundException;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Service.Interfaces.IHuespedService;
import com.isi.desa.Service.Implementations.Validators.HuespedValidator;
import com.isi.desa.Service.Interfaces.Validators.IHuespedValidator;
import com.isi.desa.Utils.Mappers.HuespedMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//@Service //Descomentar para Spring Boot
public class HuespedService implements IHuespedService {
    //@Autowired //Descomentar para Spring Boot
    private final IHuespedValidator validator;
    //@Autowired //Descomentar para Spring Boot
    private final IHuespedDAO dao;

    // Instancia unica (eager singleton)
    private static final HuespedService INSTANCE = new HuespedService();

    // Constructor privado para inyeccion manual
    private HuespedService() {
        this.dao = new HuespedDAO();
        this.validator = HuespedValidator.getInstance();
    }

    // Metodo publico para obtener la instancia
    public static HuespedService getInstance() {
        return INSTANCE;
    }

    @Override
    public HuespedDTO crear(HuespedDTO huespedDTO) throws HuespedDuplicadoException {

        // Validacion
        CannotCreateHuespedException validation = this.validator.validateCreate(huespedDTO);
        if (validation != null) {
            throw validation;
        }

        // Persistencia de Direccion
        DireccionDAO direccionDAO = new DireccionDAO();
        try {
            // intenta obtener la direccion
            direccionDAO.obtener(huespedDTO.direccion);
        } catch (Exception e) {
            // si no existe se crea
            direccionDAO.crear(huespedDTO.direccion);
        }

        // Persistencia del Huesped
        Huesped creado = dao.crear(huespedDTO);

        // Convertir a DTO para devolver
        return HuespedMapper.entityToDTO(creado);
    }

    @Override
    public BajaHuespedResultDTO eliminar(BajaHuespedRequestDTO requestDTO) {
        BajaHuespedResultDTO res = new BajaHuespedResultDTO();

        res.resultado.id = 0; // Exito
        res.resultado.mensaje = "Huesped eliminado exitosamente.";

        RuntimeException validation = this.validator.validateDelete(requestDTO.idHuesped);
        if (validation != null) {
            res.resultado.id = 2; // Error de validacion
            res.resultado.mensaje = validation.getMessage();
            return res;
        }

        Huesped eliminado = dao.eliminar(requestDTO.idHuesped);
        if (eliminado == null) {
            res.resultado.id = 1; // Error interno
            res.resultado.mensaje = "No se pudo eliminar el huesped.";
            return res;
        }

        res.huesped = HuespedMapper.entityToDTO(eliminado);
        return res;
    }

    @Override
    public BuscarHuespedResultDTO buscarHuesped(BuscarHuespedRequestDTO requestDTO) {
        BuscarHuespedResultDTO resultDTO = new BuscarHuespedResultDTO();
        resultDTO.resultado = new Resultado();
        resultDTO.huespedesEncontrados = new ArrayList<>();

        List<HuespedDTO> huespedesEncontrados;

        if (requestDTO == null || requestDTO.huesped == null) {
            // Si no hay filtros, devolver todos los huespedes NO eliminados
            List<HuespedDTO> allHuespedes = dao.leerHuespedes().stream()
                    .filter(h -> h != null && !h.isEliminado())
                    .map(HuespedMapper::entityToDTO)
                    .collect(Collectors.toList());
            huespedesEncontrados = allHuespedes;

        } else {
            HuespedDTO filtros = requestDTO.huesped;

            huespedesEncontrados = dao.leerHuespedes().stream()
                    .filter(h -> h != null && !h.isEliminado())
                    .filter(h -> (filtros.nombre == null || (h.getNombre() != null && h.getNombre().equalsIgnoreCase(filtros.nombre))) &&
                            (filtros.apellido == null || (h.getApellido() != null && h.getApellido().equalsIgnoreCase(filtros.apellido))) &&
                            (filtros.numDoc == null || (h.getNumDoc() != null && h.getNumDoc().equals(filtros.numDoc))))
                    .map(HuespedMapper::entityToDTO)
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

    public ModificarHuespedResultDTO modificar(ModificarHuespedRequestDTO request) {
        ModificarHuespedResultDTO res = new ModificarHuespedResultDTO();
        res.resultado = new Resultado();
        res.resultado.id = 1; // por defecto error interno generico

        try {
            if (request == null || request.huesped == null) {
                res.resultado.id = 2;
                res.resultado.mensaje = "Solicitud invalida: no se enviaron datos de huesped.";
                return res;
            }

            HuespedDTO dto = request.huesped;

            // Validar
            CannotModifyHuespedEsception errorValidacion = this.validator.validateUpdate(dto);
            if (errorValidacion != null) {
                res.resultado.id = 2;
                res.resultado.mensaje = errorValidacion.getMessage();
                return res;
            }

            // Duplicado de tipoDoc + numDoc
            boolean duplicado = dao.leerHuespedes().stream()
                    .filter(h -> h != null && !h.isEliminado())
                    .anyMatch(h ->
                            h.getIdHuesped() != null &&
                                    !h.getIdHuesped().equalsIgnoreCase(dto.idHuesped) &&
                                    h.getTipoDocumento() != null &&
                                    dto.tipoDocumento != null &&
                                    h.getTipoDocumento().getTipoDocumento() != null &&
                                    h.getTipoDocumento().getTipoDocumento().equalsIgnoreCase(dto.tipoDocumento.tipoDocumento) &&
                                    h.getNumDoc() != null &&
                                    h.getNumDoc().equals(dto.numDoc)
                    );

            if (duplicado && (request.aceptarIgualmente == null || !request.aceptarIgualmente)) {
                res.resultado.id = 3; // advertencia
                res.resultado.mensaje = "¡CUIDADO! El tipo y numero de documento ya existen en el sistema";
                return res; // la UI luego puede reintentar con ACEPTAR IGUALMENTE
            }


            // aplicar modificacion
            Huesped modificado = dao.modificar(dto);

            if (modificado == null) {
                res.resultado.id = 1;
                res.resultado.mensaje = "Error interno al modificar huesped.";
                return res;
            }
            res.resultado.id = 0;
            res.resultado.mensaje = "La operacion ha culminado con exito";
            return res;

        } catch (HuespedNotFoundException nf) {
            res.resultado.id = 2;
            res.resultado.mensaje = nf.getMessage();
            return res;
        } catch (Exception e) {
            res.resultado.id = 1;
            res.resultado.mensaje = "Error interno al modificar huesped: " + e.getMessage();
            return res;
        }
    }
}
