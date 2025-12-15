package com.isi.desa.Service.Implementations.Validators;

import com.isi.desa.Dao.Interfaces.IHuespedDAO;
import com.isi.desa.Dao.Interfaces.ITipoDocumentoDAO;
import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Dto.TipoDocumento.TipoDocumentoDTO;
import com.isi.desa.Exceptions.Direccion.InvalidDirectionException;
import com.isi.desa.Exceptions.Huesped.*;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Model.Entities.Tipodocumento.TipoDocumento;
import com.isi.desa.Service.Implementations.Validators.Huesped.CuitOpcionalValidator;
import com.isi.desa.Service.Implementations.Validators.Huesped.CuitValidatorFactory;
import com.isi.desa.Service.Implementations.Validators.Huesped.ResponsableInscriptoCuitValidator;
import com.isi.desa.Service.Interfaces.Validators.IDireccionValidator;
import com.isi.desa.Service.Interfaces.Validators.IHuespedValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class HuespedValidator implements IHuespedValidator {

    @Autowired
    private IHuespedDAO dao;

    @Autowired
    private ITipoDocumentoDAO tipoDocumentoDAO;

    @Autowired
    private IDireccionValidator direccionValidator;

    // --- REGEX PATTERNS ---
    private static final String TEXTO_REGEX = "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$";
    private static final String NUMERO_REGEX = "^[0-9]+$";

    @Override
    public CannotCreateHuespedException validateCreate(HuespedDTO huespedDTO) {
        List<String> errores = new ArrayList<>();

        if (huespedDTO == null) return new CannotCreateHuespedException("El DTO es nulo");

        // Validaciones texto
        if (isBlank(huespedDTO.nombre)) errores.add("El nombre es obligatorio");
        else if (!isValidText(huespedDTO.nombre)) errores.add("El nombre solo puede contener letras.");

        if (isBlank(huespedDTO.apellido)) errores.add("El apellido es obligatorio");
        else if (!isValidText(huespedDTO.apellido)) errores.add("El apellido solo puede contener letras.");

        if (isBlank(huespedDTO.numDoc)) errores.add("El numero de documento es obligatorio");

        if (!isBlank(huespedDTO.telefono) && !isValidNumber(huespedDTO.telefono)) {
            errores.add("El teléfono solo debe contener números.");
        }

        String tipoDocumentoError = validateTipoDocumento(huespedDTO.tipoDocumento);
        if (tipoDocumentoError != null) errores.add(tipoDocumentoError);

        // --- VALIDACIÓN CUIT (CREATE) ---
        Object validator = CuitValidatorFactory.getInstance().getValidator(huespedDTO.posicionIva);
        String cuitError;
        if (validator instanceof ResponsableInscriptoCuitValidator riValidator) {
            cuitError = riValidator.validateCuit(huespedDTO.cuit);
        } else {
            cuitError = ((CuitOpcionalValidator) validator).validateCuit(huespedDTO.cuit);
        }
        if (cuitError != null) errores.add(cuitError);
        // --------------------------------

        String fechaNacimientoError = validateFechaNacimiento(huespedDTO.fechaNacimiento);
        if (fechaNacimientoError != null) errores.add(fechaNacimientoError);

        if (huespedDTO.direccion != null) {
            try {
                RuntimeException dirError = direccionValidator.validate(huespedDTO.direccion);
                if (dirError != null) errores.add(dirError.getMessage());
            } catch (Exception e) {
                errores.add("Error validando dirección: " + e.getMessage());
            }
        } else {
            errores.add("La dirección es obligatoria.");
        }

        if (!errores.isEmpty()) {
            String mensajeFinal = String.join("; ", errores);
            return new CannotCreateHuespedException(mensajeFinal);
        }

        return null;
    }

    @Override
    public CannotModifyHuespedEsception validateUpdate(HuespedDTO dto) {
        List<String> errores = new ArrayList<>();

        if (dto == null) {
            return new CannotModifyHuespedEsception("No se han proporcionado datos del huesped a modificar.");
        }

        if (isBlank(dto.idHuesped)) {
            errores.add("El ID del huesped es obligatorio para modificar.");
        }

        // Validaciones de formato si vienen los datos
        if (dto.nombre != null && !isValidText(dto.nombre)) errores.add("El nombre solo puede contener letras.");
        if (dto.apellido != null && !isValidText(dto.apellido)) errores.add("El apellido solo puede contener letras.");
        if (dto.telefono != null && !isValidNumber(dto.telefono)) errores.add("El teléfono solo debe contener números.");

        String tipoDocumentoError = validateTipoDocumento(dto.tipoDocumento);
        if (tipoDocumentoError != null) errores.add(tipoDocumentoError);


        Object validator = CuitValidatorFactory.getInstance().getValidator(dto.posicionIva);
        String cuitError;

        if (validator instanceof ResponsableInscriptoCuitValidator riValidator) {
            cuitError = riValidator.validateCuit(dto.cuit);
        } else {
            cuitError = ((CuitOpcionalValidator) validator).validateCuit(dto.cuit);
        }

        if (cuitError != null) errores.add(cuitError);
        // --------------------------------

        if (!errores.isEmpty()) {
            String mensajeFinal = String.join("; ", errores);
            return new CannotModifyHuespedEsception(mensajeFinal);
        }

        return null;
    }

    public CannotDeleteHuespedException validateDelete(String idHuesped) {
        RuntimeException errorFormato = this.validateExists(idHuesped);
        if (errorFormato != null) {
            return new CannotDeleteHuespedException(errorFormato.getMessage());
        }
        if (!this.dao.obtenerEstadiasDeHuesped(idHuesped).isEmpty()) {
            return new CannotDeleteHuespedException("No se puede eliminar el huesped porque tiene estadias asociadas.");
        }
        return null;
    }

    @Override
    public RuntimeException validateExists(String idHuesped) {
        if (isBlank(idHuesped)) {
            return new IllegalArgumentException("El idHuesped es obligatorio para verificar existencia");
        }
        if (this.dao.getById(idHuesped) == null) {
            return new HuespedNotFoundException("No existe un huesped con el ID: " + idHuesped);
        }
        return null;
    }

    // --- MÉTODOS PRIVADOS ---

    private boolean isValidText(String text) {
        return Pattern.matches(TEXTO_REGEX, text);
    }

    private boolean isValidNumber(String text) {
        return Pattern.matches(NUMERO_REGEX, text);
    }

    private String validateTipoDocumento(TipoDocumentoDTO tipoDto) {
        if (tipoDto == null || isBlank(tipoDto.tipoDocumento)) {
            return "El tipo de documento es obligatorio";
        }
        if (this.tipoDocumentoDAO.obtener(tipoDto.tipoDocumento) == null) {
            return "El tipo de documento ingresado no existe en el sistema";
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

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}