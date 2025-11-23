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
    private HuespedMapper mapper; // ✔ CORREGIDO → mapper Bean

    @Override
    public HuespedDTO crear(HuespedDTO huespedDTO) throws HuespedDuplicadoException {

        CannotCreateHuespedException validation = validator.validateCreate(huespedDTO);
        if (validation != null) throw validation;

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

        if (duplicado)
            throw new HuespedDuplicadoException("Ya existe un huésped con ese tipo y número de documento.");

        try {
            direccionDAO.obtener(huespedDTO.direccion);
        } catch (Exception e) {
            direccionDAO.crear(huespedDTO.direccion);
        }

        Huesped creado = dao.crear(huespedDTO);
        return mapper.entityToDTO(creado); // ✔ CORREGIDO
    }

    @Override
    public BajaHuespedResultDTO eliminar(BajaHuespedRequestDTO requestDTO) {
        BajaHuespedResultDTO res = new BajaHuespedResultDTO();

        res.resultado.id = 0;
        res.resultado.mensaje = "Huesped eliminado exitosamente.";

        RuntimeException validation = validator.validateDelete(requestDTO.idHuesped);
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

        res.huesped = mapper.entityToDTO(eliminado); // ✔ CORREGIDO
        return res;
    }

    @Override
    public BuscarHuespedResultDTO buscarHuesped(BuscarHuespedRequestDTO req) {

        BuscarHuespedResultDTO res = new BuscarHuespedResultDTO();
        res.resultado = new Resultado();
        res.huespedesEncontrados = new ArrayList<>();

        HuespedDTO filtro = (req == null) ? null : req.huesped;
        List<Huesped> todos = dao.leerHuespedes();

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
                coincide &= h.getNombre() != null &&
                        h.getNombre().toLowerCase().contains(filtro.nombre.toLowerCase());
            }

            if (filtro.apellido != null && !filtro.apellido.isEmpty()) {
                coincide &= h.getApellido() != null &&
                        h.getApellido().toLowerCase().contains(filtro.apellido.toLowerCase());
            }

            if (filtro.tipoDocumento != null) {
                coincide &= h.getTipoDocumento() != null &&
                        h.getTipoDocumento().equalsIgnoreCase(filtro.tipoDocumento.tipoDocumento);
            }

            if (filtro.numDoc != null && !filtro.numDoc.isEmpty()) {
                coincide &= h.getNumDoc() != null &&
                        h.getNumDoc().equalsIgnoreCase(filtro.numDoc);
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
                res.resultado.mensaje = "Solicitud inválida: no se enviaron datos.";
                return res;
            }

            HuespedDTO dto = request.huesped;

            CannotModifyHuespedEsception errorValidacion = validator.validateUpdate(dto);
            if (errorValidacion != null) {
                res.resultado.id = 2;
                res.resultado.mensaje = errorValidacion.getMessage();
                return res;
            }

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
                res.resultado.mensaje = "El tipo + número de documento ya existen.";
                return res;
            }

            Huesped modificado = dao.modificar(dto);

            if (modificado == null) {
                res.resultado.id = 1;
                res.resultado.mensaje = "Error interno.";
                return res;
            }

            res.resultado.id = 0;
            res.resultado.mensaje = "Operación exitosa.";
            return res;

        } catch (HuespedNotFoundException nf) {
            res.resultado.id = 2;
            res.resultado.mensaje = nf.getMessage();
            return res;

        } catch (Exception e) {
            res.resultado.id = 1;
            res.resultado.mensaje = "Error interno: " + e.getMessage();
            return res;
        }
    }
}
