package com.isi.desa.Service.Implementations.Validators;

import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.isi.desa.Dao.Implementations.HuespedDAO;
import com.isi.desa.Dao.Implementations.TipoDocumentoDAO;
import com.isi.desa.Dao.Interfaces.IHuespedDAO;
import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Dto.TipoDocumento.TipoDocumentoDTO;
import com.isi.desa.Exceptions.Direccion.InvalidDirectionException;
import com.isi.desa.Exceptions.Huesped.*;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Model.Entities.Direccion.Direccion;
import com.isi.desa.Model.Entities.Tipodocumento.TipoDocumento;
import com.isi.desa.Service.Interfaces.Validators.IDireccionValidator;
import com.isi.desa.Service.Interfaces.Validators.IHuespedValidator;
import com.isi.desa.Utils.Format.DateFormat;
import com.isi.desa.Utils.Mappers.DireccionMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class HuespedValidator implements IHuespedValidator {
    private final IDireccionValidator direccionValidator;
    private final IHuespedDAO dao;

    // Instancia unica (eager singleton)
    private static final HuespedValidator INSTANCE = new HuespedValidator();

    // Constructor privado
    private HuespedValidator() {
        this.dao = new HuespedDAO();
        this.direccionValidator = DireccionValidator.getInstance();
    }

    // Metodo publico para obtener la instancia
    public static HuespedValidator getInstance() {
        return INSTANCE;
    }

    public Huesped create(HuespedDTO huespedDTO) {
        CannotCreateHuespedException errorValidacion = validateCreate(huespedDTO);
        if (errorValidacion != null) throw errorValidacion;

        Direccion direccion = DireccionMapper.dtoToEntity(huespedDTO.direccion);
        TipoDocumento tipoDocumento = new TipoDocumento(huespedDTO.tipoDocumento.tipoDocumento);
        LocalDate fechaNacimiento = huespedDTO.fechaNacimiento;
        return new Huesped(
                huespedDTO.nombre,
                huespedDTO.apellido,
                tipoDocumento,
                huespedDTO.numDoc,
                huespedDTO.posicionIva,
                huespedDTO.cuit,
                fechaNacimiento,
                huespedDTO.telefono,
                huespedDTO.email,
                huespedDTO.ocupacion,
                huespedDTO.nacionalidad,
                direccion,
                huespedDTO.idHuesped
        );
    }

    @Override
    public CannotCreateHuespedException validateCreate(HuespedDTO huespedDTO) {
        List<RuntimeException> errores = new ArrayList<>();

        if (isBlank(huespedDTO.nombre)) errores.add(new IllegalArgumentException("El nombre es obligatorio"));
        if (isBlank(huespedDTO.apellido)) errores.add(new IllegalArgumentException("El apellido es obligatorio"));
        if (isBlank(huespedDTO.numDoc)) errores.add(new IllegalArgumentException("El numero de documento es obligatorio"));

        String tipoDocumentoError = validateTipoDocumento(huespedDTO.tipoDocumento);
        if (tipoDocumentoError != null) errores.add(new IllegalArgumentException(tipoDocumentoError));

        if(huespedDTO.cuit != null) {
            String cuitError = validateCuit(huespedDTO.cuit);
            if (cuitError != null) errores.add(new IllegalArgumentException(cuitError));
        }

        String fechaNacimientoError = validateFechaNacimiento(huespedDTO.fechaNacimiento);
        if (fechaNacimientoError != null) errores.add(new IllegalArgumentException(fechaNacimientoError));

        InvalidDirectionException direccionValidationError = direccionValidator.validate(huespedDTO.direccion);
        if (direccionValidationError != null) errores.add(direccionValidationError);


        if (!errores.isEmpty()) {
            return new CannotCreateHuespedException(errores.stream().map(
                    RuntimeException::getMessage).reduce(
                    (a, b) -> a + "; " + b)
                    .orElse("")
            );
        }

        return null;
    }

    @Override
    public CannotDeleteHuespedException validateDelete(String idHuesped) {
        RuntimeException huespedNoExistente = this.validateExists(idHuesped);
        if(huespedNoExistente != null) {
            return new CannotDeleteHuespedException(huespedNoExistente.getMessage());
        }
        Huesped huesperAEliminar = this.dao.getById(idHuesped);
        if (huesperAEliminar == null) {
            return new CannotDeleteHuespedException(
                    new HuespedNotFoundException("No existe un huesped con el idHuesped proporcionado")
                            .getMessage()
            );
        }
        if (!huesperAEliminar.getIdsEstadias().isEmpty()) {
            return new CannotDeleteHuespedException(
                    new HuespedConEstadiaAsociadasException("No se puede eliminar el huesped porque tiene estadias asociadas")
                            .getMessage()
            );
        }

        return null;
    }

    @Override
    public CannotModifyHuespedEsception validateUpdate(HuespedDTO dto) {
        List<RuntimeException> errores = new ArrayList<>();

        if (dto == null) {
            errores.add(new IllegalArgumentException("No se han proporcionado datos del huesped a modificar."));
            return new CannotModifyHuespedEsception(errores.stream().map(
                    RuntimeException::getMessage).reduce(
                            (a, b) -> a + "; " + b)
                    .orElse("")
            );
        }

        String tipoDocumentoError = validateTipoDocumento(dto.tipoDocumento);
        if (tipoDocumentoError != null) errores.add(new IllegalArgumentException(tipoDocumentoError));

        if(!isBlank(dto.cuit)) {
            String cuitError = validateCuit(dto.cuit);
            if (cuitError != null) errores.add(new IllegalArgumentException(cuitError));
        }

        if (!errores.isEmpty()) return new CannotModifyHuespedEsception(errores.stream().map(
                        RuntimeException::getMessage).reduce(
                        (a, b) -> a + "; " + b)
                .orElse("")
        );

        return null;
    }

    @Override
    public RuntimeException validateExists(String idHuesped) {
        if (idHuesped == null || idHuesped.trim().isEmpty()) {
            return new IllegalArgumentException("El idHuesped es obligatorio para verificar existencia");
        }
        if(this.dao.getById(idHuesped) == null) {
            return new HuespedNotFoundException("No existe un huesped con el idHuesped proporcionado");
        }
        return null;
    }

    private String validateTipoDocumento(TipoDocumentoDTO tipoDocumentoDTO) {
        TipoDocumentoDAO dao = new TipoDocumentoDAO();
        if (tipoDocumentoDTO == null) {
            return "El tipo de documento es obligatorio";
        }

        String tipo = tipoDocumentoDTO.tipoDocumento;

        if (isBlank(tipo)) {
            return "El nombre del tipo de documento es obligatorio";
        }

        TipoDocumento tipodocumentoencontrado = dao.obtener(tipo);
        if(tipodocumentoencontrado == null) {
            return "El tipo de documento ingresado no existe";
        }

        return null;
    }


    private String validateCuit(String cuit) {
        // validar formato CUIT: XX-XXXXXXXX-X
        if (cuit != null && !cuit.trim().isEmpty()) {
            String regex = "\\d{2}-\\d{8}-\\d{1}";
            if (!cuit.matches(regex)) {
                return "El CUIT debe tener el formato XX-XXXXXXXX-X";
            }
        }
        return null;
    }

    private String validateFechaNacimiento(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) {
            return "La fecha de nacimiento es obligatoria";
        }
        if (fechaNacimiento.isAfter(LocalDate.now())) {
            return "La fecha de nacimiento no puede ser futura";
        }

        return null;
    }

    // Helper
    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
