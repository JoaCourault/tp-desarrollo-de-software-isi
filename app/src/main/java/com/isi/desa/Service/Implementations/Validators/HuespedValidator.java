package com.isi.desa.Service.Implementations.Validators;

import com.isi.desa.Dao.Interfaces.IDireccionDAO;
import com.isi.desa.Dao.Interfaces.IHuespedDAO;
import com.isi.desa.Dao.Interfaces.ITipoDocumentoDAO;
import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Dto.TipoDocumento.TipoDocumentoDTO;
import com.isi.desa.Exceptions.Direccion.InvalidDirectionException;
import com.isi.desa.Exceptions.Huesped.*;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Model.Entities.Direccion.Direccion;
import com.isi.desa.Model.Entities.Tipodocumento.TipoDocumento;
import com.isi.desa.Service.Interfaces.Validators.IDireccionValidator;
import com.isi.desa.Service.Interfaces.Validators.IHuespedValidator;
import com.isi.desa.Utils.Mappers.DireccionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class HuespedValidator implements IHuespedValidator {

    @Autowired
    private IDireccionValidator direccionValidator;

    @Autowired
    @Qualifier("huespedDAO")
    private IHuespedDAO dao;

    @Autowired
    private ITipoDocumentoDAO tipoDocumentoDAO;

    // REGEX: Permite letras (mayús/minús), vocales con tilde, ñ y espacios.
    // Rechaza números y símbolos (.,-/)
    private static final String TEXT_ONLY_REGEX = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$";
    private static final Pattern TEXT_PATTERN = Pattern.compile(TEXT_ONLY_REGEX);

    @Override
    public CannotCreateHuespedException validateCreate(HuespedDTO huespedDTO) {
        List<RuntimeException> errores = new ArrayList<>();

        // 1. Validar Nombre (Obligatorio + Solo Texto)
        if (isBlank(huespedDTO.nombre)) {
            errores.add(new IllegalArgumentException("El nombre es obligatorio"));
        } else if (!isValidText(huespedDTO.nombre)) {
            errores.add(new IllegalArgumentException("El nombre contiene caracteres inválidos (solo letras y espacios)"));
        }

        // 2. Validar Apellido (Obligatorio + Solo Texto)
        if (isBlank(huespedDTO.apellido)) {
            errores.add(new IllegalArgumentException("El apellido es obligatorio"));
        } else if (!isValidText(huespedDTO.apellido)) {
            errores.add(new IllegalArgumentException("El apellido contiene caracteres inválidos"));
        }

        // 3. Validar Nacionalidad (Solo Texto)
        if (!isBlank(huespedDTO.nacionalidad) && !isValidText(huespedDTO.nacionalidad)) {
            errores.add(new IllegalArgumentException("La nacionalidad contiene caracteres inválidos"));
        }

        // 4. Validar Ocupación (Solo Texto)
        if (!isBlank(huespedDTO.ocupacion) && !isValidText(huespedDTO.ocupacion)) {
            errores.add(new IllegalArgumentException("La ocupación contiene caracteres inválidos"));
        }

        if (isBlank(huespedDTO.numDoc)) errores.add(new IllegalArgumentException("El numero de documento es obligatorio"));

        String tipoDocumentoError = validateTipoDocumento(huespedDTO.tipoDocumento);
        if (tipoDocumentoError != null) errores.add(new IllegalArgumentException(tipoDocumentoError));

        // Validación Responsable Inscripto
        if ("Responsable Inscripto".equalsIgnoreCase(huespedDTO.posicionIva)) {
            if (isBlank(huespedDTO.cuit)) {
                errores.add(new IllegalArgumentException("El CUIT es obligatorio para la condición 'Responsable Inscripto'"));
            }
        }

        if(huespedDTO.cuit != null) {
            String cuitError = validateCuit(huespedDTO.cuit);
            if (cuitError != null) errores.add(new IllegalArgumentException(cuitError));
        }

        String fechaNacimientoError = validateFechaNacimiento(huespedDTO.fechaNacimiento);
        if (fechaNacimientoError != null) errores.add(new IllegalArgumentException(fechaNacimientoError));

        if (huespedDTO.direccion != null) {
            InvalidDirectionException direccionValidationError = direccionValidator.validate(huespedDTO.direccion);
            if (direccionValidationError != null) errores.add(direccionValidationError);
        }

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
        if (!this.dao.obtenerEstadiasDeHuesped(huesperAEliminar.getIdHuesped()).isEmpty()) {
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
            return new CannotModifyHuespedEsception("Datos nulos");
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

    // Helper para crear entidad (si se necesita aquí)
    public Huesped create(HuespedDTO huespedDTO) {
        // Lógica de mapeo básica si es requerida por la interfaz
        return null;
    }

    // --- MÉTODOS PRIVADOS ---

    private String validateTipoDocumento(TipoDocumentoDTO tipoDocumentoDTO) {
        if (tipoDocumentoDTO == null) return "El tipo de documento es obligatorio";
        String tipo = tipoDocumentoDTO.tipoDocumento;
        if (isBlank(tipo)) return "El nombre del tipo de documento es obligatorio";
        TipoDocumento tipodocumentoencontrado = tipoDocumentoDAO.obtener(tipo);
        if(tipodocumentoencontrado == null) return "El tipo de documento ingresado no existe";
        return null;
    }

    private String validateCuit(String cuit) {
        if (cuit != null && !cuit.trim().isEmpty()) {
            String regex = "\\d{2}-\\d{8}-\\d{1}";
            if (!cuit.matches(regex)) return "El CUIT debe tener el formato XX-XXXXXXXX-X";
        }
        return null;
    }

    private String validateFechaNacimiento(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) return "La fecha de nacimiento es obligatoria";
        if (fechaNacimiento.isAfter(LocalDate.now())) return "La fecha de nacimiento no puede ser futura";
        return null;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    // NUEVO: Validación de caracteres especiales
    private boolean isValidText(String text) {
        return text != null && TEXT_PATTERN.matcher(text).matches();
    }
}