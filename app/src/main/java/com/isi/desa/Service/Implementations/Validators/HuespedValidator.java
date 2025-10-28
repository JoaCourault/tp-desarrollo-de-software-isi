package com.isi.desa.Service.Implementations.Validators;

import com.isi.desa.Dao.Implementations.HuespedDAO;
import com.isi.desa.Dao.Implementations.TipoDocumentoDAO;
import com.isi.desa.Dao.Interfaces.IHuespedDAO;
import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Dto.TipoDocumento.TipoDocumentoDTO;
import com.isi.desa.Exceptions.HuespedConEstadiaAsociadasException;
import com.isi.desa.Exceptions.HuespedNotFoundException;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Model.Entities.Direccion.Direccion;
import com.isi.desa.Model.Entities.Tipodocumento.TipoDocumento;
import com.isi.desa.Service.Interfaces.Validators.IDireccionValidator;
import com.isi.desa.Service.Interfaces.Validators.IHuespedValidator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// @Service //Descomentar para correr con Spring Boot
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
        List<String> errores = validateCreate(huespedDTO);
        if (errores != null && !errores.isEmpty()) {
            throw new IllegalArgumentException(String.join(", ", errores));
        }
        Direccion direccion = this.direccionValidator.create(huespedDTO.direccion);
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

    public List<String> validateCreate(HuespedDTO huespedDTO) {
        List<String> errores = new ArrayList<>();
        String error;
        error = validateIdHuesped(huespedDTO.idHuesped); if (error != null) errores.add(error);
        error = validateNombre(huespedDTO.nombre); if (error != null) errores.add(error);
        error = validateApellido(huespedDTO.apellido); if (error != null) errores.add(error);
        error = validateTipoDocumento(huespedDTO.tipoDocumento); if (error != null) errores.add(error);
        error = validateNumDoc(huespedDTO.numDoc); if (error != null) errores.add(error);
        error = validateCuit(huespedDTO.cuit); if (error != null) errores.add(error);
        if (huespedDTO.direccion != null) {
            errores.addAll(direccionValidator.validate(huespedDTO.direccion));
        } else {
            errores.add("La direccion es obligatoria");
        }
        // Agrega mas validaciones segun los campos necesarios
        return errores.isEmpty() ? null : errores;
    }

    public String validateIdHuesped(String idHuesped) {
        return (idHuesped == null || idHuesped.trim().isEmpty()) ? "El idHuesped es obligatorio" : null;
    }
    public String validateNombre(String nombre) {
        return (nombre == null || nombre.trim().isEmpty()) ? "El nombre es obligatorio" : null;
    }
    public String validateApellido(String apellido) {
        return (apellido == null || apellido.trim().isEmpty()) ? "El apellido es obligatorio" : null;
    }

    @Override
    public String validateTipoDocumento(String tipoDocumento) {
        return (tipoDocumento == null || tipoDocumento.trim().isEmpty()) ? "El tipo de documento es obligatorio" : null;
    }

    public String validateTipoDocumento(TipoDocumentoDTO tipoDocumentoDTO) {
        TipoDocumentoDAO dao = new TipoDocumentoDAO();
        if (tipoDocumentoDTO == null) {
            return "El tipo de documento es obligatorio";
        }

        String tipo = tipoDocumentoDTO.tipoDocumento;

        if (tipo == null || tipo.trim().isEmpty()) {
            return "El nombre del tipo de documento es obligatorio";
        }

        TipoDocumento tipodocumentoencontrado = dao.obtener(tipo);
        if(tipodocumentoencontrado == null) {
            return "El tipo de documento ingresado no existe";
        }

        return null;
    }
    public String validateNumDoc(String numDoc) {
        return (numDoc == null || numDoc.trim().isEmpty()) ? "El numero de documento es obligatorio" : null;
    }

    @Override
    public String validateCuit(Integer cuit) {
        return (cuit == null) ? "El CUIT es un campo obligatorio" : null;
    }

    @Override
    public RuntimeException validateDelete(String idHuesped) {
        RuntimeException huespedNoExistente = this.validateExists(idHuesped);
        if(huespedNoExistente != null) {
            return huespedNoExistente;
        }
        Huesped huesperAEliminar = this.dao.getById(idHuesped);
        if (huesperAEliminar == null) {
            return new HuespedNotFoundException("No existe un huesped con el idHuesped proporcionado");
        }
        if (!huesperAEliminar.getIdsEstadias().isEmpty()) {
            return new HuespedConEstadiaAsociadasException("No se puede eliminar el huesped porque tiene estadias asociadas");
        }

        return null;
    }

    @Override
    public RuntimeException validateExists(String idHuesped) {
        if (idHuesped == null || idHuesped.trim().isEmpty()) {
            return new RuntimeException("El idHuesped es obligatorio para verificar existencia");
        }
        if(this.dao.getById(idHuesped) == null) {
            return new HuespedNotFoundException("No existe un huesped con el idHuesped proporcionado");
        }
        return null;
    }

    public String validateCuit(String cuit) {
        if (cuit == null || cuit.trim().isEmpty()) return "El CUIT es obligatorio";

        // validar formato CUIT: XX-XXXXXXXX-X
        if (cuit != null && !cuit.trim().isEmpty()) {
            String regex = "\\d{2}-\\d{8}-\\d{1}";
            if (!cuit.matches(regex)) {
                return "El CUIT debe tener el formato XX-XXXXXXXX-X";
            }
        }
        return null;
    }
    public String validateFechaNacimiento(LocalDate fechaNacimiento) {
        return (fechaNacimiento == null) ? "La fecha de nacimiento es un campo obligatorio" : null;
    }

    // HuespedValidator.java
    public List<String> validateUpdate(HuespedDTO dto) {
        List<String> errores = new ArrayList<>();

        if (dto == null) {
            errores.add("No se enviaron datos de huesped");
            return errores;
        }

        // Requeridos basicos
        if (isBlank(dto.nombre)) {
            errores.add("El nombre es obligatorio");
        }
        if (isBlank(dto.apellido)) {
            errores.add("El apellido es obligatorio");
        }
        if (dto.tipoDocumento == null || isBlank(dto.tipoDocumento.tipoDocumento)) {
            errores.add("El tipo de documento es obligatorio");
        }
        if (isBlank(dto.numDoc)) {
            errores.add("El numero de documento es obligatorio");
        }

        // Direccion en MODIFICACIoN:
        // - Debe existir una direccion (no nula)
        // - Si viene con id => se asume referencia valida y NO se exige calle/nro/etc.
        // - Si NO viene id => se interpreta como direccion nueva y se validan campos minimos
        if (dto.direccion == null) {
            errores.add("La direccion es obligatoria");
        } else {
            // ¿Referencia existente?
            if (!isBlank(dto.direccion.id)) {
                // OK: direccion existente, no validar mas campos
            } else {
                // Direccion nueva: validar minimos
                if (isBlank(dto.direccion.calle)) {
                    errores.add("La calle de la direccion es obligatoria");
                }
                if (dto.direccion.numero == null) {
                    errores.add("El numero de la direccion es obligatorio");
                }
                if (dto.direccion.codigoPostal == null) {
                    errores.add("El codigo postal de la direccion es obligatorio");
                }
                if (isBlank(dto.direccion.localidad)) {
                    errores.add("La localidad de la direccion es obligatoria");
                }
                if (isBlank(dto.direccion.provincia)) {
                    errores.add("La provincia de la direccion es obligatoria");
                }
                if (isBlank(dto.direccion.pais)) {
                    errores.add("El pais de la direccion es obligatorio");
                }
            }
        }

        return errores;
    }

    // Helper
    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }



}
