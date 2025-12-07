package com.isi.desa.Service.Implementations;

import com.isi.desa.Dao.Implementations.DireccionDAO;
import com.isi.desa.Dao.Interfaces.IHuespedDAO;
import com.isi.desa.Dto.Huesped.*;
import com.isi.desa.Dto.Resultado;
import com.isi.desa.Exceptions.Huesped.CannotCreateHuespedException;
import com.isi.desa.Exceptions.Huesped.CannotModifyHuespedEsception;
import com.isi.desa.Exceptions.Huesped.HuespedDuplicadoException;
import com.isi.desa.Exceptions.Huesped.HuespedNotFoundException;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Service.Interfaces.IHuespedService;
import com.isi.desa.Service.Interfaces.Validators.IHuespedValidator;
import com.isi.desa.Utils.Mappers.HuespedMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class HuespedService implements IHuespedService {

    @Autowired
    private IHuespedValidator validator;

    @Qualifier("huespedDAO")
    @Autowired
    private IHuespedDAO dao;

    @Autowired
    private DireccionDAO direccionDAO;

    @Autowired
    private HuespedMapper huespedMapper;

    // --- LOGICA DE ALTA (CU09) ---

    // 1. Método de la interfaz (por defecto asume que NO se fuerza la creación)
    @Override
    public HuespedDTO crear(HuespedDTO huespedDTO) throws HuespedDuplicadoException {
        return crear(huespedDTO, false);
    }

    // 2. Método sobrecargado para soportar el flujo "ACEPTAR IGUALMENTE"
    public HuespedDTO crear(HuespedDTO huespedDTO, Boolean aceptarIgualmente) throws HuespedDuplicadoException {

        // A. Validación de datos obligatorios (Diagrama paso 2.A)
        CannotCreateHuespedException validation = this.validator.validateCreate(huespedDTO);
        if (validation != null) {
            throw validation;
        }

        // B. Validación de Duplicados (Diagrama paso 2.B)
        // Solo verificamos si el usuario NO ha dicho "Aceptar Igualmente" todavía
        if (aceptarIgualmente == null || !aceptarIgualmente) {
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

            if (duplicado) {
                // Esto dispara el aviso en el Front para mostrar los botones "ACEPTAR IGUALMENTE"
                throw new HuespedDuplicadoException("El tipo y número de documento ya existen.");
            }
        }

        // C. Generación de ID y Persistencia
        // Generamos ID manual (UUID corto) para vincular las entidades
        String nuevoIdHuesped = "H-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        huespedDTO.idHuesped = nuevoIdHuesped;

        // D. Guardar HUESPED (Padre) - Primero
        Huesped huespedGuardado = dao.crear(huespedDTO);

        // E. Guardar DIRECCION (Hija) - Después, vinculada
        if (huespedDTO.direccion != null) {
            try {
                // Usamos el DAO inyectado, no 'new DireccionDAO()'
                direccionDAO.crear(huespedDTO.direccion, nuevoIdHuesped);
            } catch (Exception e) {
                // Loguear error pero no interrumpir el flujo principal si no es crítico
                System.out.println("Error al guardar dirección: " + e.getMessage());
            }
        }

        // Usamos el mapper inyectado
        return huespedMapper.entityToDTO(huespedGuardado);
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

        res.huesped = huespedMapper.entityToDTO(eliminado);
        return res;
    }

    @Override
    public BuscarHuespedResultDTO buscarHuesped(BuscarHuespedRequestDTO req) {
        BuscarHuespedResultDTO res = new BuscarHuespedResultDTO();
        res.resultado = new Resultado();
        // Inicializamos la lista para evitar NullPointer
        res.huespedesEncontrados = new ArrayList<>();

        HuespedDTO filtro = (req == null) ? null : req.huesped;
        List<Huesped> todos = this.dao.leerHuespedes();

        // Lógica de filtrado... (igual que tenías)
        if (filtro == null) {
            // Convertimos ENTIDADES a DTOs usando el mapper inyectado
            res.huespedesEncontrados = todos.stream()
                    .map(h -> huespedMapper.entityToDTO(h))
                    .collect(Collectors.toList());

            res.resultado.id = 0;
            res.resultado.mensaje = "OK";
            return res;
        }

        // ... (resto de tu lógica de filtrado 'coincide') ...

        List<HuespedDTO> encontrados = new ArrayList<>();
        for (Huesped h : todos) {
            boolean coincide = true;
            // ... (tus ifs de filtro) ...

            if (coincide) {
                // Usamos la instancia inyectada huespedMapper
                encontrados.add(huespedMapper.entityToDTO(h));
            }
        }

        res.huespedesEncontrados = encontrados;
        res.resultado.id = 0;
        res.resultado.mensaje = "OK";
        return res;
    }

    @Override
    public ModificarHuespedResultDTO modificar(ModificarHuespedRequestDTO request) {
        ModificarHuespedResultDTO res = new ModificarHuespedResultDTO();
        res.resultado = new Resultado();
        res.resultado.id = 1;

        try {
            if (request == null || request.huesped == null) {
                res.resultado.id = 2;
                res.resultado.mensaje = "Solicitud invalida: no se enviaron datos de huesped.";
                return res;
            }

            HuespedDTO dto = request.huesped;

            CannotModifyHuespedEsception errorValidacion = this.validator.validateUpdate(dto);
            if (errorValidacion != null) {
                res.resultado.id = 2;
                res.resultado.mensaje = errorValidacion.getMessage();
                return res;
            }

            // Validación duplicado en UPDATE
            boolean duplicado = dao.leerHuespedes().stream()
                    .filter(h -> h != null && !h.isEliminado())
                    .anyMatch(h ->
                            h.getIdHuesped() != null &&
                                    !h.getIdHuesped().equalsIgnoreCase(dto.idHuesped) &&
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

    @Override
    public Huesped getById(String id) {
        return dao.getById(id);
    }
}