package com.isi.desa.Service.Implementations.Validators;

import com.isi.desa.Dao.Interfaces.IHuespedDAO;
import com.isi.desa.Dao.Interfaces.ITipoDocumentoDAO;
import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Dto.TipoDocumento.TipoDocumentoDTO;
import com.isi.desa.Exceptions.Direccion.InvalidDirectionException;
import com.isi.desa.Exceptions.Huesped.*;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Model.Entities.Direccion.Direccion;
import com.isi.desa.Model.Entities.Tipodocumento.TipoDocumento;
import com.isi.desa.Service.Interfaces.Validators.IHuespedValidator;
import com.isi.desa.Utils.Mappers.DireccionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class HuespedValidator implements IHuespedValidator {

    @Autowired
    private IHuespedDAO dao;

    @Autowired
    private ITipoDocumentoDAO tipoDocumentoDAO; // Usamos la interfaz

    @Autowired
    private DireccionMapper direccionMapper;

    @Autowired
    private DireccionValidator direccionValidator; // Inyectado como componente


    private static final String TEXT_ONLY_REGEX = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$";
    private static final Pattern TEXT_PATTERN = Pattern.compile(TEXT_ONLY_REGEX);

    public Huesped create(HuespedDTO huespedDTO) {
        CannotCreateHuespedException errorValidacion = validateCreate(huespedDTO);
        if (errorValidacion != null) throw errorValidacion;

        Direccion direccion = direccionMapper.dtoToEntity(huespedDTO.direccion);

        TipoDocumento tipoDocumento = new TipoDocumento();
        tipoDocumento.setTipoDocumento(huespedDTO.tipoDoc.tipoDocumento);

        LocalDate fechaNacimiento = huespedDTO.fechaNac;

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

        // 1. Validar Nombre
        if (isBlank(huespedDTO.nombre)) {
            errores.add(new IllegalArgumentException("El nombre es obligatorio"));
        } else if (!isValidText(huespedDTO.nombre)) {
            errores.add(new IllegalArgumentException("El nombre contiene caracteres inválidos (solo letras y espacios)"));
        }

        // 2. Validar Apellido
        if (isBlank(huespedDTO.apellido)) {
            errores.add(new IllegalArgumentException("El apellido es obligatorio"));
        } else if (!isValidText(huespedDTO.apellido)) {
            errores.add(new IllegalArgumentException("El apellido contiene caracteres inválidos"));
        }

        // 3. Validar Nacionalidad
        if (!isBlank(huespedDTO.nacionalidad) && !isValidText(huespedDTO.nacionalidad)) {
            errores.add(new IllegalArgumentException("La nacionalidad contiene caracteres inválidos"));
        }

        // 4. Validar Ocupación
        if (!isBlank(huespedDTO.ocupacion) && !isValidText(huespedDTO.ocupacion)) {
            errores.add(new IllegalArgumentException("La ocupación contiene caracteres inválidos"));
        }

        if (isBlank(huespedDTO.numDoc)) errores.add(new IllegalArgumentException("El numero de documento es obligatorio"));

        String tipoDocumentoError = validateTipoDocumento(huespedDTO.tipoDoc);
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

        String fechaNacimientoError = validateFechaNacimiento(huespedDTO.fechaNac);
        if (fechaNacimientoError != null) errores.add(new IllegalArgumentException(fechaNacimientoError));

        // Validación de dirección delegada al validador inyectado
        if (huespedDTO.direccion != null) {
            try {
                InvalidDirectionException dirError = direccionValidator.validate(huespedDTO.direccion);
                if (dirError != null) errores.add(dirError);
            } catch (Exception e) {
                errores.add(new IllegalArgumentException("Error validando dirección: " + e.getMessage()));
            }
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

        // 2. Validar existencia real en BD usando el DAO inyectado
        Huesped huespedAEliminar = this.dao.getById(idHuesped);
        if (huespedAEliminar == null) {
            return new CannotDeleteHuespedException("No existe un huesped con el ID proporcionado.");
        }

        // 3. Validar reglas de negocio (Estadías)
        if (!this.dao.obtenerEstadiasDeHuesped(huespedAEliminar.getIdHuesped()).isEmpty()) {
            return new CannotDeleteHuespedException("No se puede eliminar el huesped porque tiene estadias asociadas.");
        }
        return null;
    }

    @Override
    public CannotModifyHuespedEsception validateUpdate(HuespedDTO dto) {
        List<RuntimeException> errores = new ArrayList<>();

        if (dto == null) {
            return new CannotModifyHuespedEsception("No se han proporcionado datos del huesped a modificar.");
        }

        if (isBlank(dto.idHuesped)) {
            errores.add(new IllegalArgumentException("El ID del huesped es obligatorio para modificar."));
        }

        // Validamos Tipo Documento
        String tipoDocumentoError = validateTipoDocumento(dto.tipoDoc);
        if (tipoDocumentoError != null) errores.add(new IllegalArgumentException(tipoDocumentoError));

        // Validamos CUIT
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
        if (tipoDocumentoDTO == null) {
            return "El tipo de documento es obligatorio";
        }

        String tipo = tipoDocumentoDTO.tipoDocumento;

        if (isBlank(tipo)) {
            return "El nombre del tipo de documento es obligatorio";
        }

        // Usamos el DAO inyectado
        TipoDocumento tipodocumentoencontrado = tipoDocumentoDAO.obtener(tipo);

        if(tipodocumentoencontrado == null) {
            return "El tipo de documento ingresado (" + tipo + ") no existe en la base de datos";
        }

        return null;
    }

    private String validateCuit(String cuit) {
        if (cuit != null && !cuit.trim().isEmpty()) {
            // Regex básico de CUIT
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

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private boolean isValidText(String text) {
        return text != null && TEXT_PATTERN.matcher(text).matches();
    }
}