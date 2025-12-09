package com.isi.desa.Service.Implementations;

// Asegúrate de importar la Interfaz o la Clase de DireccionDAO según corresponda
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

    @Autowired
    @Qualifier("huespedDAO") // CORRECCIÓN: Resuelve la ambigüedad del Bean
    private IHuespedDAO dao;

    @Autowired // CORRECCIÓN: Inyección de dependencia en lugar de 'new'
    private DireccionDAO direccionDAO;

    // CORRECCIÓN: Se eliminó el patrón Singleton manual (getInstance) porque Spring ya es Singleton.

    @Override
    public HuespedDTO crear(HuespedDTO huespedDTO) throws HuespedDuplicadoException {

        // Validación básica
        CannotCreateHuespedException validation = this.validator.validateCreate(huespedDTO);
        if (validation != null) {
            throw validation;
        }

        // Validación de duplicados (Optimización: Se podría mover a una Query en el Repo)
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
            throw new HuespedDuplicadoException(
                    "Ya existe un huésped con ese tipo y número de documento."
            );
        }

        // Persistencia de Dirección (Usando el Bean inyectado)
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
}