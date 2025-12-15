package com.isi.desa.Service.Implementations;

import com.isi.desa.Dao.Implementations.DireccionDAO;
import com.isi.desa.Dao.Interfaces.IHuespedDAO;
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

    @Override
    @Transactional
    public HuespedDTO crear(HuespedDTO huespedDTO, Boolean aceptarIgualmente) throws HuespedDuplicadoException {
        // 1. Validación
        CannotCreateHuespedException validation = this.validator.validateCreate(huespedDTO);
        if (validation != null) {
            throw validation;
        }

        // 2. Validación duplicados
        if (aceptarIgualmente == null || !aceptarIgualmente) {
            boolean duplicado = dao.leerHuespedes().stream()
                    .filter(h -> h != null && !h.isEliminado())
                    .anyMatch(h ->
                            h.getTipoDoc() != null &&
                                    huespedDTO.tipoDoc != null &&
                                    huespedDTO.tipoDoc.tipoDocumento != null &&
                                    h.getTipoDoc().getTipoDocumento().equalsIgnoreCase(huespedDTO.tipoDoc.tipoDocumento) &&
                                    h.getNumDoc() != null &&
                                    h.getNumDoc().equalsIgnoreCase(huespedDTO.numDoc)
                    );

            if (duplicado) {
                throw new HuespedDuplicadoException("Ya existe un huésped con ese documento.");
            }
        }

        // 3. Persistencia de Dirección
        if (huespedDTO.direccion != null) {
            Direccion dirGuardada = direccionDAO.crear(huespedDTO.direccion);
            if (dirGuardada != null) {
                huespedDTO.direccion.id = dirGuardada.getIdDireccion();
            }
        }

        // 4. Persistencia del huésped
        Huesped creado = dao.crear(huespedDTO);

        // 2. USO DE INSTANCIA INYECTADA (this.huespedMapper)
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
        List<Huesped> todos = this.dao.leerHuespedes();

        if (filtro == null) {
            // Convertimos la lista completa a DTOs
            res.huespedesEncontrados = convertirLista(todos);
            res.resultado.id = 0;
            res.resultado.mensaje = "OK";
            return res;
        }

        boolean algunCampo =
                (filtro.nombre != null && !filtro.nombre.isEmpty()) ||
                        (filtro.apellido != null && !filtro.apellido.isEmpty()) ||
                        (filtro.tipoDoc != null && filtro.tipoDoc.tipoDocumento != null) ||
                        (filtro.numDoc != null && !filtro.numDoc.isEmpty());

        if (!algunCampo) {
            res.huespedesEncontrados = convertirLista(todos);
            res.resultado.id = 0;
            res.resultado.mensaje = "OK";
            return res;
        }

        // Filtrado
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

            if (filtro.tipoDoc != null && filtro.tipoDoc.tipoDocumento != null && !filtro.tipoDoc.tipoDocumento.isEmpty()) {
                coincide &= (
                        h.getTipoDoc() != null &&
                                h.getTipoDoc().getTipoDocumento().equalsIgnoreCase(filtro.tipoDoc.tipoDocumento)
                );
            }

            if (filtro.numDoc != null && !filtro.numDoc.isEmpty()) {
                if (h.getNumDoc() == null) { coincide = false; }
                else { coincide &= h.getNumDoc().equalsIgnoreCase(filtro.numDoc); }
            }

            if (coincide) {
                // 3. CONVERSIÓN: Entidad -> DTO antes de agregar a la lista de respuesta
                res.huespedesEncontrados.add(this.huespedMapper.entityToDTO(h));
            }
        }

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

            // Validación duplicado
            boolean duplicado = dao.leerHuespedes().stream()
                    .filter(h -> h != null && !h.isEliminado())
                    .anyMatch(h ->
                            h.getIdHuesped() != null &&
                                    !h.getIdHuesped().equalsIgnoreCase(dto.idHuesped) &&
                                    h.getTipoDoc() != null &&
                                    dto.tipoDoc != null &&
                                    dto.tipoDoc.tipoDocumento != null &&
                                    h.getTipoDoc().getTipoDocumento().equalsIgnoreCase(dto.tipoDoc.tipoDocumento) &&
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
            e.printStackTrace(); // Agregado para ver error en consola si pasa algo raro
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