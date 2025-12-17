package com.isi.desa.Service.Implementations;

import com.isi.desa.Dao.Implementations.DireccionDAO;
import com.isi.desa.Dao.Interfaces.IHuespedDAO;
import com.isi.desa.Dao.Repositories.EstadiaRepository; // <--- IMPORTANTE
import com.isi.desa.Dao.Repositories.HuespedRepository;
import com.isi.desa.Dto.Huesped.*;
import com.isi.desa.Dto.Resultado;
import com.isi.desa.Exceptions.Huesped.CannotCreateHuespedException;
import com.isi.desa.Exceptions.Huesped.CannotModifyHuespedEsception;
import com.isi.desa.Exceptions.Huesped.HuespedDuplicadoException;
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
import java.util.Optional;

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

    @Autowired
    private HuespedRepository repository;

    @Autowired
    private EstadiaRepository estadiaRepository;


    @Override
    @Transactional
    public HuespedDTO crear(HuespedDTO huespedDTO, Boolean aceptarIgualmente) throws HuespedDuplicadoException {

        CannotCreateHuespedException validation = this.validator.validateCreate(huespedDTO);
        if (validation != null) throw validation;

        if (aceptarIgualmente == null || !aceptarIgualmente) {
            boolean existe = repository.existsByNumDocAndTipoDoc_TipoDocumento(
                    huespedDTO.numDoc, huespedDTO.tipoDoc.tipoDocumento);
            if (existe) throw new HuespedDuplicadoException("¡CUIDADO! El tipo y numero de documento ya existen en el sistema");
        }

        if (huespedDTO.direccion != null) {
            Direccion dirGuardada = direccionDAO.crear(huespedDTO.direccion);
            if (dirGuardada != null) huespedDTO.direccion.id = dirGuardada.getIdDireccion();
        }

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

        // 1. Validar ID
        RuntimeException validation = this.validator.validateDelete(requestDTO.idHuesped);
        if (validation != null) {
            res.resultado.id = 2;
            res.resultado.mensaje = validation.getMessage();
            return res;
        }

        // 2. Buscar si existe en la BDD
        Optional<Huesped> opHuesped = repository.findById(requestDTO.idHuesped);
        if (opHuesped.isEmpty()) {
            res.resultado.id = 1;
            res.resultado.mensaje = "El huésped no existe.";
            return res;
        }
        Huesped huespedTarget = opHuesped.get();

        // 3. VALIDACIÓN DE NEGOCIO:
        // Verificamos si durmió allí (huespedesHospedados) O si fue titular de alguna estadía (huesped)
        boolean tieneEstadias = estadiaRepository.existsByHuespedesHospedados_IdHuesped(requestDTO.idHuesped)
                || estadiaRepository.existsByTitularId(requestDTO.idHuesped);

        if (tieneEstadias) {
            res.resultado.id = 2; // Error de negocio
            // Mensaje claro para el Frontend
            res.resultado.mensaje = "No se puede eliminar: El huésped posee historial de estadías en el hotel.";
            return res;
        }

        // 4. BAJA LÓGICA (Update eliminado = true)
        // No borramos físicamente, solo marcamos la bandera.
        huespedTarget.setEliminado(true);
        Huesped huespedGuardado = repository.save(huespedTarget);

        // 5. Retorno Exitoso
        res.resultado.id = 0;
        res.resultado.mensaje = "Huesped dado de baja exitosamente.";
        res.huesped = this.huespedMapper.entityToDTO(huespedGuardado);

        return res;
    }

    @Override
    public BuscarHuespedResultDTO buscarHuesped(BuscarHuespedRequestDTO req) {

        BuscarHuespedResultDTO res = new BuscarHuespedResultDTO();
        res.resultado = new Resultado();
        res.huespedesEncontrados = new ArrayList<>();
        HuespedDTO filtro = (req == null) ? null : req.huesped;

        boolean hayFiltros = false;
        if (filtro != null) {
            hayFiltros = (filtro.nombre != null && !filtro.nombre.isBlank()) ||
                    (filtro.apellido != null && !filtro.apellido.isBlank()) ||
                    (filtro.numDoc != null && !filtro.numDoc.isBlank()) ||
                    (filtro.tipoDoc != null && filtro.tipoDoc.tipoDocumento != null && !filtro.tipoDoc.tipoDocumento.isBlank());
        }

        List<Huesped> resultadosEntidad;
        if (!hayFiltros) {
            resultadosEntidad = this.dao.leerHuespedes();
        } else {
            resultadosEntidad = this.dao.buscarHuesped(filtro);
        }
        res.huespedesEncontrados = convertirLista(resultadosEntidad);
        res.resultado.id = 0;
        res.resultado.mensaje = "OK";
        return res;
    }

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
            if (request == null || request.huesped == null) {
                res.resultado.id = 2; res.resultado.mensaje = "Datos vacíos."; return res;
            }
            HuespedDTO dto = request.huesped;
            if (dto.idHuesped == null || dto.idHuesped.isBlank()) {
                res.resultado.id = 2; res.resultado.mensaje = "El ID del huésped es obligatorio."; return res;
            }
            CannotModifyHuespedEsception errorValidacion = this.validator.validateUpdate(dto);
            if (errorValidacion != null) {
                res.resultado.id = 2; res.resultado.mensaje = errorValidacion.getMessage(); return res;
            }
            if (request.aceptarIgualmente == null || !request.aceptarIgualmente) {
                boolean existeOtro = repository.existsByNumDocAndTipoDoc_TipoDocumentoAndIdHuespedNot(
                        dto.numDoc, dto.tipoDoc.tipoDocumento, dto.idHuesped
                );
                if (existeOtro) {
                    res.resultado.id = 3; res.resultado.mensaje = "El documento ya existe en otro huésped."; return res;
                }
            }
            if (dto.direccion == null || dto.direccion.id == null) {
                res.resultado.id = 2; res.resultado.mensaje = "Error de integridad: Dirección inválida."; return res;
            }
            direccionDAO.modificar(dto.direccion);
            Huesped modificado = dao.modificar(dto);
            if (modificado == null) {
                res.resultado.id = 1; res.resultado.mensaje = "No se encontró el huésped."; return res;
            }
            res.resultado.id = 0; res.resultado.mensaje = "Huésped modificado exitosamente.";
            return res;
        } catch (RuntimeException re) {
            re.printStackTrace();
            res.resultado.id = 2; res.resultado.mensaje = re.getMessage(); return res;
        } catch (Exception e) {
            e.printStackTrace();
            res.resultado.id = 1; res.resultado.mensaje = "Error interno: " + e.getMessage(); return res;
        }
    }

    @Override
    public Huesped getById(String id) {
        return dao.getById(id);
    }
}