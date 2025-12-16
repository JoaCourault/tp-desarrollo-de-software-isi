package com.isi.desa.Service.Implementations;

import com.isi.desa.Dao.Implementations.DireccionDAO;
import com.isi.desa.Dao.Interfaces.IHuespedDAO;
import com.isi.desa.Dao.Repositories.HuespedRepository;
import com.isi.desa.Dto.Huesped.*;
import com.isi.desa.Dto.Resultado;
import com.isi.desa.Exceptions.Huesped.CannotCreateHuespedException;
import com.isi.desa.Exceptions.Huesped.CannotModifyHuespedEsception;
import com.isi.desa.Exceptions.Huesped.HuespedDuplicadoException;
import com.isi.desa.Exceptions.Huesped.HuespedNotFoundException;
import com.isi.desa.Model.Entities.Direccion.Direccion;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Service.Interfaces.IHuespedService;
import com.isi.desa.Service.Interfaces.Validators.IHuespedValidator;
import com.isi.desa.Utils.Mappers.HuespedMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class HuespedService implements IHuespedService {

    @Autowired
    private IHuespedValidator validator;

    @Qualifier("huespedDAO")
    @Autowired
    private IHuespedDAO dao;

    @Autowired
    private DireccionDAO direccionDAO;

    // 1. INYECCIÓN DEL MAPPER
    @Autowired
    private HuespedMapper huespedMapper;

    @Autowired
    private HuespedRepository repository;

    @Override
    @Transactional
    public HuespedDTO crear(HuespedDTO huespedDTO, Boolean aceptarIgualmente) throws HuespedDuplicadoException {
        // 1. Validación de campos obligatorios
        CannotCreateHuespedException validation = this.validator.validateCreate(huespedDTO);
        if (validation != null) {
            throw validation;
        }
        // 2. Validación duplicados
        if (aceptarIgualmente == null || !aceptarIgualmente) {
            boolean existe = repository.existsByNumDocAndTipoDoc_TipoDocumento(
                    huespedDTO.numDoc,
                    huespedDTO.tipoDoc.tipoDocumento
            );

            if (existe) {
                throw new HuespedDuplicadoException("¡CUIDADO! El tipo y numero de documento ya existen en el sistema");
            }
        }

        if (huespedDTO.direccion != null) {
            Direccion dirGuardada = direccionDAO.crear(huespedDTO.direccion);
            if (dirGuardada != null) {
                // Vinculamos el ID generado al DTO para que Hibernate sepa que ya existe
                huespedDTO.direccion.id = dirGuardada.getIdDireccion();
            }
        }

        // 4. Persistencia del huésped
        Huesped creado = dao.crear(huespedDTO);

        return this.huespedMapper.entityToDTO(creado);
    }

    @Override
    public HuespedDTO crear(HuespedDTO huespedDTO) throws HuespedDuplicadoException {
        return crear(huespedDTO, false);
    }

    @Override
    public BajaHuespedResultDTO eliminar(BajaHuespedRequestDTO requestDTO) {
        BajaHuespedResultDTO res = new BajaHuespedResultDTO();
        res.resultado = new Resultado();
        res.resultado.id = 0;
        res.resultado.mensaje = "Huesped eliminado exitosamente.";

        RuntimeException validation = this.validator.validateDelete(requestDTO.idHuesped);
        if (validation != null) {
            res.resultado.id = 2;
            res.resultado.mensaje = validation.getMessage();
            return res;
        }

        Huesped eliminado = dao.eliminar(requestDTO.idHuesped);
        if (eliminado == null) {
            res.resultado.id = 1;
            res.resultado.mensaje = "No se pudo eliminar el huesped.";
            return res;
        }

        // 2. USO DE INSTANCIA INYECTADA
        res.huesped = this.huespedMapper.entityToDTO(eliminado);
        return res;
    }

    @Override
    public BuscarHuespedResultDTO buscarHuesped(BuscarHuespedRequestDTO req) {
        BuscarHuespedResultDTO res = new BuscarHuespedResultDTO();
        res.resultado = new Resultado();
        res.huespedesEncontrados = new ArrayList<>();

        HuespedDTO filtro = (req == null) ? null : req.huesped;

        // Validamos si hay algún filtro cargado
        boolean hayFiltros = false;
        if (filtro != null) {
            hayFiltros = (filtro.nombre != null && !filtro.nombre.isBlank()) ||
                    (filtro.apellido != null && !filtro.apellido.isBlank()) ||
                    (filtro.numDoc != null && !filtro.numDoc.isBlank()) ||
                    (filtro.tipoDoc != null && filtro.tipoDoc.tipoDocumento != null && !filtro.tipoDoc.tipoDocumento.isBlank());
        }

        List<Huesped> resultadosEntidad;

        if (!hayFiltros) {
            // buscarTodosLosHuesped()
            resultadosEntidad = this.dao.leerHuespedes();
        } else {
            resultadosEntidad = this.dao.buscarHuesped(filtro);
        }

        // Convertimos Entidades a DTOs
        res.huespedesEncontrados = convertirLista(resultadosEntidad);

        res.resultado.id = 0;
        res.resultado.mensaje = "OK";
        return res;
    }

    // Método auxiliar para convertir listas masivas
    private List<HuespedDTO> convertirLista(List<Huesped> entidades) {
        List<HuespedDTO> dtos = new ArrayList<>();
        if (entidades != null) {
            for (Huesped h : entidades) {
                dtos.add(this.huespedMapper.entityToDTO(h));
            }
        }
        return dtos;
    }

    @Override
    @Transactional
    public ModificarHuespedResultDTO modificar(ModificarHuespedRequestDTO request) {
        ModificarHuespedResultDTO res = new ModificarHuespedResultDTO();
        res.resultado = new Resultado();

        try {
            // 1. Validaciones Básicas (Null checks)
            if (request == null || request.huesped == null) {
                res.resultado.id = 2;
                res.resultado.mensaje = "Datos vacíos.";
                return res;
            }

            HuespedDTO dto = request.huesped;

            if (dto.idHuesped == null || dto.idHuesped.isBlank()) {
                res.resultado.id = 2;
                res.resultado.mensaje = "El ID del huésped es obligatorio para modificar.";
                return res;
            }

            // Validaciones de Regex y Negocio
            CannotModifyHuespedEsception errorValidacion = this.validator.validateUpdate(dto);
            if (errorValidacion != null) {
                res.resultado.id = 2;
                res.resultado.mensaje = errorValidacion.getMessage();
                return res;
            }

            // 2. Validación de Duplicados
            if (request.aceptarIgualmente == null || !request.aceptarIgualmente) {
                boolean existeOtro = repository.existsByNumDocAndTipoDoc_TipoDocumentoAndIdHuespedNot(
                        dto.numDoc,
                        dto.tipoDoc.tipoDocumento,
                        dto.idHuesped
                );

                if (existeOtro) {
                    res.resultado.id = 3; // Activa modal "Aceptar Igualmente"
                    res.resultado.mensaje = "El documento ya existe en otro huésped.";
                    return res;
                }
            }

            // 3. Actualización de Dirección (ESTRICTA)
            if (dto.direccion == null || dto.direccion.id == null || dto.direccion.id.isBlank()) {
                res.resultado.id = 2;
                res.resultado.mensaje = "Error de integridad: El huésped no tiene una dirección válida para modificar.";
                return res;
            }

            // Llamamos al DAO. Si el ID no existe en BD, el DAO lanzará RuntimeException
            // y caeremos en el catch de abajo.
            direccionDAO.modificar(dto.direccion);


            // 4. Actualización del Huésped
            Huesped modificado = dao.modificar(dto);

            if (modificado == null) {
                res.resultado.id = 1;
                res.resultado.mensaje = "No se encontró el huésped a modificar.";
                return res;
            }

            // Éxito
            res.resultado.id = 0;
            res.resultado.mensaje = "Huésped modificado exitosamente.";
            return res;

        } catch (RuntimeException re) {
            // Capturamos la excepción específica del DAO de Dirección
            re.printStackTrace();
            res.resultado.id = 2; // Error de validación/lógica
            res.resultado.mensaje = re.getMessage(); // "No se encontró la dirección con ID..."
            return res;

        } catch (Exception e) {
            e.printStackTrace();
            res.resultado.id = 1; // Error interno
            res.resultado.mensaje = "Error interno: " + e.getMessage();
            return res;
        }
    }

    @Override
    public Huesped getById(String id) {
        return dao.getById(id);
    }
}