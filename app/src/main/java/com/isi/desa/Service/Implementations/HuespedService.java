package com.isi.desa.Service.Implementations;

import com.isi.desa.Dao.Interfaces.IDireccionDAO;
import com.isi.desa.Dao.Interfaces.IHuespedDAO;
import com.isi.desa.Dto.Huesped.*;
import com.isi.desa.Dto.Resultado;
import com.isi.desa.Exceptions.Huesped.CannotCreateHuespedException;
import com.isi.desa.Exceptions.Huesped.CannotModifyHuespedEsception;
import com.isi.desa.Exceptions.Huesped.HuespedNotFoundException;
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

@Service
public class HuespedService implements IHuespedService {

    @Autowired
    private IHuespedValidator validator;

    @Autowired
    @Qualifier("huespedDAO")
    private IHuespedDAO dao;
    @Autowired
    private IDireccionDAO direccionDAO; // <--- Inyectamos el DAO de dirección
    @Autowired
    private IDireccionValidator direccionValidator;

    @Override
    public AltaHuespedResultDTO crear(AltaHuespedRequestDTO request) {
        AltaHuespedResultDTO response = new AltaHuespedResultDTO();
        response.resultado = new Resultado();

        try {
            // 1. Validar request
            if (request == null || request.huesped == null) {
                response.resultado.id = 2;
                response.resultado.mensaje = "Datos de huésped no proporcionados.";
                return response;
            }

            HuespedDTO huespedDTO = request.huesped;

            // 2. Validaciones de negocio (Campos obligatorios)
            CannotCreateHuespedException validationError = this.validator.validateCreate(huespedDTO);
            if (validationError != null) {
                response.resultado.id = 2;
                response.resultado.mensaje = validationError.getMessage();
                return response;
            }

            // 3. Chequeo de DUPLICADOS
            boolean duplicado = dao.leerHuespedes().stream()
                    .filter(h -> h != null && !h.isEliminado())
                    .anyMatch(h ->
                            h.getTipoDocumento() != null &&
                                    huespedDTO.tipoDocumento != null &&
                                    huespedDTO.tipoDocumento.tipoDocumento != null &&
                                    h.getTipoDocumento().equalsIgnoreCase(huespedDTO.tipoDocumento.tipoDocumento) &&
                                    h.getNumDoc() != null &&
                                    h.getNumDoc().equalsIgnoreCase(huespedDTO.numDoc)
                    );

            // --- LÓGICA DE ADVERTENCIA ---
            if (duplicado) {
                // Si es duplicado Y NO confirmaron la acción -> Devolvemos ADVERTENCIA (id=3)
                if (request.aceptarIgualmente == null || !request.aceptarIgualmente) {
                    response.resultado.id = 3; // 3 = Warning
                    response.resultado.mensaje = "Ya existe un huésped con ese tipo y número de documento. ¿Desea crearlo de todos modos?";
                    return response;
                }
                // Si request.aceptarIgualmente es TRUE, el código sigue y crea el registro.
            }

            // 4. Persistencia
            // El DAO maneja la creación (y la dirección por Cascade)
            Huesped creado = dao.crear(huespedDTO);

            // 5. Respuesta Éxito
            response.huesped = HuespedMapper.entityToDTO(creado);
            response.resultado.id = 0; // 0 = Éxito
            response.resultado.mensaje = "Huésped creado exitosamente.";

        } catch (Exception e) {
            response.resultado.id = 1; // Error interno
            response.resultado.mensaje = "Error interno al crear huésped: " + e.getMessage();
        }

        return response;
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

            if (filtro.tipoDocumento != null) {
                coincide &= (
                        h.getTipoDocumento() != null &&
                                h.getTipoDocumento().equalsIgnoreCase(filtro.tipoDocumento.tipoDocumento)
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

            // 1. Validar Huesped (Datos básicos)
            CannotModifyHuespedEsception errorHuesped = this.validator.validateUpdate(dto);
            if (errorHuesped != null) {
                res.resultado.id = 2;
                res.resultado.mensaje = errorHuesped.getMessage();
                return res;
            }

            // 2. LOGICA DE DIRECCIÓN (Corrección del ID)
            if (dto.direccion != null) {

                // --- TRUCO: Si el DTO no trae ID de dirección, lo buscamos en la BD ---
                if (dto.direccion.id == null || dto.direccion.id.isBlank()) {
                    Huesped huespedActual = dao.getById(dto.idHuesped);
                    if (huespedActual != null && huespedActual.getDireccion() != null) {
                        // Rescatamos el ID existente para que el validador no falle
                        dto.direccion.id = huespedActual.getDireccion().getIdDireccion();
                    }
                }
                // ---------------------------------------------------------------------

                // A. Ahora sí validamos (ya debería tener ID)
                RuntimeException errorDir = this.direccionValidator.validateUpdate(dto.direccion);
                if (errorDir != null) {
                    res.resultado.id = 2;
                    res.resultado.mensaje = "Error en Dirección: " + errorDir.getMessage();
                    return res;
                }

                // B. Actualizar Dirección independientemente
                this.direccionDAO.modificar(dto.direccion);
            }

            // Validación duplicado en UPDATE
            boolean duplicado = dao.leerHuespedes().stream()
                    .filter(h -> h != null && !h.isEliminado())
                    .anyMatch(h ->
                            h.getIdHuesped() != null &&
                                    !h.getIdHuesped().equalsIgnoreCase(dto.idHuesped) && // No soy yo mismo
                                    h.getTipoDocumento() != null &&
                                    dto.tipoDocumento != null &&
                                    h.getTipoDocumento().equalsIgnoreCase(dto.tipoDocumento.tipoDocumento) &&
                                    h.getNumDoc().equals(dto.numDoc)

                    );


            if (duplicado && (request.aceptarIgualmente == null || !request.aceptarIgualmente)) {
                res.resultado.id = 3;
                res.resultado.mensaje = "¡CUIDADO! El tipo y numero de documento ya existen en el sistema";
                return res;

            }

            // 4. Modificar Huesped
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