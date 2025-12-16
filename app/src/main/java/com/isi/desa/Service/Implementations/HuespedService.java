package com.isi.desa.Service.Implementations;

import com.isi.desa.Dao.Interfaces.IDireccionDAO;
import com.isi.desa.Dao.Interfaces.IHuespedDAO;
import com.isi.desa.Dto.Huesped.*;
import com.isi.desa.Dto.Resultado;
import com.isi.desa.Exceptions.Huesped.CannotCreateHuespedException;
import com.isi.desa.Exceptions.Huesped.CannotModifyHuespedEsception;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Service.Interfaces.IHuespedService;
import com.isi.desa.Service.Interfaces.Validators.IDireccionValidator;
import com.isi.desa.Service.Interfaces.Validators.IHuespedValidator;
import com.isi.desa.Utils.Mappers.HuespedMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class HuespedService implements IHuespedService {

    @Autowired
    private IHuespedValidator validator;

    @Autowired
    @Qualifier("huespedDAO")
    private IHuespedDAO dao;

    @Autowired
    private IDireccionDAO direccionDAO;

    @Autowired
    private IDireccionValidator direccionValidator;

    @Override
    @Transactional
    public AltaHuespedResultDTO crear(AltaHuespedRequestDTO request) {

        AltaHuespedResultDTO response = new AltaHuespedResultDTO();
        response.resultado = new Resultado();

        try {
            if (request == null || request.huesped == null) {
                response.resultado.id = 2;
                response.resultado.mensaje = "Datos de huésped no proporcionados.";
                return response;
            }

            HuespedDTO huespedDTO = request.huesped;

            // Validación del huésped
            CannotCreateHuespedException validationError = this.validator.validateCreate(huespedDTO);
            if (validationError != null) {
                response.resultado.id = 2;
                response.resultado.mensaje = validationError.getMessage();
                return response;
            }

            //  Validación de dirección (si existe)
            if (huespedDTO.direccion != null) {
                RuntimeException errorDir = this.direccionValidator.validate(huespedDTO.direccion);
                if (errorDir != null) {
                    response.resultado.id = 2;
                    response.resultado.mensaje = "Error en Dirección: " + errorDir.getMessage();
                    return response;
                }
            }

            // Chequeo de duplicados
            boolean duplicado = dao.leerHuespedes().stream()
                    .filter(h -> h != null && !h.isEliminado())
                    .anyMatch(h ->
                            h.getTipoDocumento() != null &&
                                    huespedDTO.tipoDocumento != null &&
                                    huespedDTO.tipoDocumento.tipoDocumento != null &&
                                    h.getTipoDocumento().getTipoDocumento()
                                            .equalsIgnoreCase(huespedDTO.tipoDocumento.tipoDocumento) &&
                                    h.getNumDoc() != null &&
                                    h.getNumDoc().equalsIgnoreCase(huespedDTO.numDoc)
                    );

            if (duplicado && (request.aceptarIgualmente == null || !request.aceptarIgualmente)) {
                response.resultado.id = 3;
                response.resultado.mensaje =
                        "Ya existe un huésped con ese tipo y número de documento. ¿Desea crearlo de todos modos?";
                return response;
            }

            // Generación de IDs (FORMATO DEFINITIVO)
            if (huespedDTO.idHuesped == null || huespedDTO.idHuesped.isBlank()) {
                String uuidRaw = UUID.randomUUID().toString().replace("-", "");
                huespedDTO.idHuesped = "HU_" + uuidRaw.substring(0, 15);
            }

            if (huespedDTO.direccion != null &&
                    (huespedDTO.direccion.id == null || huespedDTO.direccion.id.isBlank())) {
                String uuidRaw = UUID.randomUUID().toString().replace("-", "");
                huespedDTO.direccion.id = "DI_" + uuidRaw.substring(0, 15);
            }

            // Mapear y guardar (cascade guarda dirección)
            Huesped entity = HuespedMapper.dtoToEntity(huespedDTO);
            entity.setEliminado(false);

            Huesped creado = dao.save(entity);

            response.huesped = HuespedMapper.entityToDTO(creado);
            response.resultado.id = 0;
            response.resultado.mensaje = "Huésped creado exitosamente.";
            return response;

        } catch (Exception e) {
            response.resultado.id = 1;
            response.resultado.mensaje = "Error interno al crear huésped: " + e.getMessage();
            return response;
        }
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
            res.resultado.mensaje = "No se pudo eliminar el huesped (puede que no exista).";
            return res;

        }
        res.huesped = HuespedMapper.entityToDTO(eliminado);
        return res;

    }

    @Override
    public BuscarHuespedResultDTO buscarHuesped(BuscarHuespedRequestDTO req) {
        BuscarHuespedResultDTO res = new BuscarHuespedResultDTO();
        res.resultado = new Resultado();
        res.huespedesEncontrados = new ArrayList<>();

        HuespedDTO filtro = (req == null) ? null : req.huesped;
        List<Huesped> todos = this.dao.leerHuespedes();

        if (filtro == null) {
            res.huespedesEncontrados = todos;
            res.resultado.id = 0;
            res.resultado.mensaje = "OK";
            return res;
        }

        boolean algunCampo =
                (filtro.nombre != null && !filtro.nombre.isEmpty()) ||
                        (filtro.apellido != null && !filtro.apellido.isEmpty()) ||
                        (filtro.tipoDocumento != null) ||
                        (filtro.numDoc != null && !filtro.numDoc.isEmpty());

        if (!algunCampo) {
            res.huespedesEncontrados = todos;
            res.resultado.id = 0;
            res.resultado.mensaje = "OK";
            return res;
        }

        for (Huesped h : todos) {
            boolean coincide = true;

            if (filtro.nombre != null && !filtro.nombre.isEmpty()) {
                if (h.getNombre() == null) { coincide = false; }
                else { coincide &= h.getNombre().toLowerCase().contains(filtro.nombre.toLowerCase()); }
            }

            if (filtro.apellido != null && !filtro.apellido.isEmpty()) {
                if (h.getApellido() == null) { coincide = false; }
                else { coincide &= h.getApellido().toLowerCase().contains(filtro.apellido.toLowerCase()); }
            }

            if (filtro.tipoDocumento != null && filtro.tipoDocumento.tipoDocumento != null) {
                // CORRECCIÓN: Comparamos objeto vs string dentro de objeto
                coincide &= (
                        h.getTipoDocumento() != null &&
                                h.getTipoDocumento().getTipoDocumento().equalsIgnoreCase(filtro.tipoDocumento.tipoDocumento)
                );
            }

            if (filtro.numDoc != null && !filtro.numDoc.isEmpty()) {
                if (h.getNumDoc() == null) { coincide = false; }
                else { coincide &= h.getNumDoc().equalsIgnoreCase(filtro.numDoc); }
            }

            if (coincide) res.huespedesEncontrados.add(h);
        }

        res.resultado.id = 0;
        res.resultado.mensaje = "OK";
        return res;
    }

    @Override
    @Transactional
    public ModificarHuespedResultDTO modificar(ModificarHuespedRequestDTO request) {
        ModificarHuespedResultDTO res = new ModificarHuespedResultDTO();
        res.resultado = new Resultado();

        try {
            if (request == null || request.huesped == null) {
                res.resultado.id = 2;
                res.resultado.mensaje = "Datos no proporcionados.";
                return res;
            }

            HuespedDTO dto = request.huesped;

            CannotModifyHuespedEsception errorHuesped = this.validator.validateUpdate(dto);
            if (errorHuesped != null) {
                res.resultado.id = 2;
                res.resultado.mensaje = errorHuesped.getMessage();
                return res;
            }

            if (dto.direccion != null) {
                if (dto.direccion.id == null || dto.direccion.id.isBlank()) {
                    Huesped huespedActual = dao.getById(dto.idHuesped);
                    if (huespedActual != null && huespedActual.getDireccion() != null) {
                        dto.direccion.id = huespedActual.getDireccion().getIdDireccion();
                    }
                }
                RuntimeException errorDir = this.direccionValidator.validateUpdate(dto.direccion);
                if (errorDir != null) {
                    res.resultado.id = 2;
                    res.resultado.mensaje = "Error en Dirección: " + errorDir.getMessage();
                    return res;
                }
                this.direccionDAO.modificar(dto.direccion);
            }

            // CORRECCIÓN EN UPDATE TAMBIÉN
            boolean duplicado = dao.leerHuespedes().stream()
                    .filter(h -> h != null && !h.isEliminado())
                    .anyMatch(h ->
                            h.getIdHuesped() != null &&
                                    !h.getIdHuesped().equalsIgnoreCase(dto.idHuesped) &&
                                    h.getTipoDocumento() != null &&
                                    dto.tipoDocumento != null &&
                                    // Accedemos al String dentro del Objeto
                                    h.getTipoDocumento().getTipoDocumento().equalsIgnoreCase(dto.tipoDocumento.tipoDocumento) &&
                                    h.getNumDoc().equals(dto.numDoc)
                    );

            if (duplicado && (request.aceptarIgualmente == null || !request.aceptarIgualmente)) {
                res.resultado.id = 3;
                res.resultado.mensaje = "¡CUIDADO! El tipo y numero de documento ya existen en el sistema";
                return res;
            }

            Huesped modificado = dao.modificar(dto);

            res.resultado.id = 0;
            res.resultado.mensaje = "La operacion ha culminado con exito";
            return res;

        } catch (Exception e) {
            res.resultado.id = 1;
            res.resultado.mensaje = "Error interno: " + e.getMessage();
            return res;
        }
    }
}