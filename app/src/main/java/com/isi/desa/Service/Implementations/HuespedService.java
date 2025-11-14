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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HuespedService implements IHuespedService {

    @Autowired
    private IHuespedValidator validator;

    @Autowired
    private IHuespedDAO dao;

    private static final HuespedService INSTANCE = new HuespedService();

    public static HuespedService getInstance() {
        return INSTANCE;
    }

    @Override
    public HuespedDTO crear(HuespedDTO huespedDTO) throws HuespedDuplicadoException {

        // Validación básica
        CannotCreateHuespedException validation = this.validator.validateCreate(huespedDTO);
        if (validation != null) {
            throw validation;
        }

        // 🔥 VALIDACIÓN DE DUPLICADOS (agregada)
        boolean duplicado = dao.leerHuespedes().stream()
                .filter(h -> h != null && !h.isEliminado())
                .anyMatch(h ->
                        h.getTipoDocumento() != null &&
                                huespedDTO.tipoDocumento != null &&
                                h.getTipoDocumento().getTipoDocumento().equalsIgnoreCase(huespedDTO.tipoDocumento.tipoDocumento) &&
                                h.getNumDoc() != null &&
                                h.getNumDoc().equalsIgnoreCase(huespedDTO.numDoc)
                );

        if (duplicado) {
            throw new HuespedDuplicadoException(
                    "Ya existe un huésped con ese tipo y número de documento."
            );
        }

        // Persistencia de Dirección
        DireccionDAO direccionDAO = new DireccionDAO();
        try {
            direccionDAO.obtener(huespedDTO.direccion);
        } catch (Exception e) {
            direccionDAO.crear(huespedDTO.direccion);
        }

        // Persistencia del huésped
        Huesped creado = dao.crear(huespedDTO);

        return HuespedMapper.entityToDTO(creado);
    }


    @Override
    public BajaHuespedResultDTO eliminar(BajaHuespedRequestDTO requestDTO) {
        BajaHuespedResultDTO res = new BajaHuespedResultDTO();

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

        res.huesped = HuespedMapper.entityToDTO(eliminado);
        return res;
    }


    @Override
    public BuscarHuespedResultDTO buscarHuesped(BuscarHuespedRequestDTO req) {
        BuscarHuespedResultDTO res = new BuscarHuespedResultDTO();
        res.resultado = new Resultado();
        res.huespedesEncontrados = new ArrayList<>();

        HuespedDTO filtro = req.huesped;

        List<Huesped> todos = this.dao.leerHuespedes();

        // Si el usuario no completa NADA → devolver todos
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

        // Filtrado
        for (Huesped h : todos) {

            boolean coincide = true;

            if (filtro.nombre != null && !filtro.nombre.isEmpty()) {
                coincide &= h.getNombre().toLowerCase().contains(filtro.nombre.toLowerCase());
            }

            if (filtro.apellido != null && !filtro.apellido.isEmpty()) {
                coincide &= h.getApellido().toLowerCase().contains(filtro.apellido.toLowerCase());
            }

            if (filtro.tipoDocumento != null) {
                coincide &= (
                        h.getTipoDocumento() != null &&
                                h.getTipoDocumento().getTipoDocumento().equalsIgnoreCase(filtro.tipoDocumento.tipoDocumento)
                );
            }

            if (filtro.numDoc != null && !filtro.numDoc.isEmpty()) {
                coincide &= h.getNumDoc().equalsIgnoreCase(filtro.numDoc);
            }

            if (coincide) res.huespedesEncontrados.add(h);
        }

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
                                    h.getTipoDocumento().getTipoDocumento().equalsIgnoreCase(dto.tipoDocumento.tipoDocumento) &&
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
}
