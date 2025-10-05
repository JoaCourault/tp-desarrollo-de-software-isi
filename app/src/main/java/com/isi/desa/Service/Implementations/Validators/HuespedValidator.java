package com.isi.desa.Service.Implementations.Validators;

import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Model.Entities.Direccion.Direccion;
import com.isi.desa.Model.Entities.Tipodocumento.TipoDocumento;
import com.isi.desa.Service.Interfaces.Validators.IDireccionValidator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//@Service //Descomentar para correr con Spring Boot
public class HuespedValidator {
    private final IDireccionValidator direccionValidator;

    public HuespedValidator() {
        this.direccionValidator = new DireccionValidator();
    }

    public Huesped create(HuespedDTO huespedDTO) {
        List<String> errores = validateCreate(huespedDTO);
        if (errores != null && !errores.isEmpty()) {
            throw new IllegalArgumentException(String.join(", ", errores));
        }
        Direccion direccion = this.direccionValidator.create(huespedDTO.direccion);
        TipoDocumento tipoDocumento = new TipoDocumento(huespedDTO.tipoDocumento);
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
                direccion
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
            errores.add("La dirección es obligatoria");
        }
        // Agrega más validaciones según los campos necesarios
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
    public String validateTipoDocumento(String tipoDocumento) {
        return (tipoDocumento == null || tipoDocumento.trim().isEmpty()) ? "El tipo de documento es obligatorio" : null;
    }
    public String validateNumDoc(String numDoc) {
        return (numDoc == null || numDoc.trim().isEmpty()) ? "El número de documento es obligatorio" : null;
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
}
