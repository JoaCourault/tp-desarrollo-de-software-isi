package com.isi.desa.Service.Implementations.Validators;

import com.isi.desa.Dao.Interfaces.IHuespedDAO;
import com.isi.desa.Dao.Interfaces.ITipoDocumentoDAO;
import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Dto.TipoDocumento.TipoDocumentoDTO;
import com.isi.desa.Exceptions.Huesped.*;
import com.isi.desa.Service.Interfaces.Validators.IDireccionValidator;
import com.isi.desa.Service.Interfaces.Validators.IHuespedValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class HuespedValidator implements IHuespedValidator {

    @Autowired
    private IHuespedDAO dao;

    @Autowired
    private ITipoDocumentoDAO tipoDocumentoDAO;

    @Autowired
    private IDireccionValidator direccionValidator;

    @Override
    public CannotCreateHuespedException validateCreate(HuespedDTO dto) {
        List<String> errores = new ArrayList<>();

        if (dto == null) {
            return new CannotCreateHuespedException("El DTO del huésped no puede ser nulo.");
        }

        // 1. Validaciones de campos simples
        if (isBlank(dto.nombre)) errores.add("El nombre es obligatorio");
        if (isBlank(dto.apellido)) errores.add("El apellido es obligatorio");
        if (isBlank(dto.numDoc)) errores.add("El numero de documento es obligatorio");

        // 2. Validación de Tipo Documento
        String tipoDocError = validateTipoDocumento(dto.tipoDocumento);
        if (tipoDocError != null) errores.add(tipoDocError);

        // 3. Validación de CUIT
        if (dto.cuit != null) {
            String cuitError = validateCuit(dto.cuit);
            if (cuitError != null) errores.add(cuitError);
        }

        // 4. Validación de Fecha Nacimiento
        String fechaNacError = validateFechaNacimiento(dto.fechaNacimiento);
        if (fechaNacError != null) errores.add(fechaNacError);

        // 5. Validación de Dirección (Delegamos al otro validador)
        if (dto.direccion != null) {
            RuntimeException dirError = direccionValidator.validate(dto.direccion);
            if (dirError != null) {
                errores.add("Error en Dirección: " + dirError.getMessage());
            }
        } else {
            // Si la dirección es obligatoria por regla de negocio:
            errores.add("La dirección es obligatoria.");
        }

        if (!errores.isEmpty()) {
            String mensaje = String.join("; ", errores);
            return new CannotCreateHuespedException(mensaje);
        }

        return null;
    }

    @Override
    public CannotModifyHuespedEsception validateUpdate(HuespedDTO dto) {
        List<String> errores = new ArrayList<>();

        if (dto == null) {
            return new CannotModifyHuespedEsception("No se han proporcionado datos del huesped a modificar.");
        }

        // Para modificar, el ID es fundamental
        if (isBlank(dto.idHuesped)) {
            errores.add("El ID del huésped es obligatorio para modificar.");
        }

        // Reutilizamos validaciones de campos si aplica
        String tipoDocError = validateTipoDocumento(dto.tipoDocumento);
        if (tipoDocError != null) errores.add(tipoDocError);

        if (!isBlank(dto.cuit)) {
            String cuitError = validateCuit(dto.cuit);
            if (cuitError != null) errores.add(cuitError);
        }

        if (!errores.isEmpty()) {
            String mensaje = String.join("; ", errores);
            return new CannotModifyHuespedEsception(mensaje);
        }

        return null;
    }

    @Override
    public CannotDeleteHuespedException validateDelete(String idHuesped) {
        // 1. Verificar existencia
        RuntimeException errorExistencia = this.validateExists(idHuesped);
        if (errorExistencia != null) {
            return new CannotDeleteHuespedException(errorExistencia.getMessage());
        }

        // 2. Verificar dependencias (Estadías)
        // Usamos el DAO para consultar la BBDD
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

    // --- MÉTODOS PRIVADOS AUXILIARES ---

    private String validateTipoDocumento(TipoDocumentoDTO tipoDto) {
        if (tipoDto == null || isBlank(tipoDto.tipoDocumento)) {
            return "El tipo de documento es obligatorio";
        }
        // Validamos contra la base de datos si existe el tipo
        if (this.tipoDocumentoDAO.obtener(tipoDto.tipoDocumento) == null) {
            return "El tipo de documento ingresado no existe en el sistema";
        }
        return null;
    }

    private String validateCuit(String cuit) {
        if (cuit != null && !cuit.trim().isEmpty()) {
            // Regex simple para XX-XXXXXXXX-X
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
}