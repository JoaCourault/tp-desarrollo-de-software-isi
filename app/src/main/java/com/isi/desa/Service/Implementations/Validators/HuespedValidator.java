package com.isi.desa.Service.Implementations.Validators;

import com.isi.desa.Dao.Implementations.TipoDocumentoDAO;
import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Dto.TipoDocumento.TipoDocumentoDTO;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Model.Entities.Direccion.Direccion;
import com.isi.desa.Model.Entities.Tipodocumento.TipoDocumento;
import com.isi.desa.Service.Interfaces.Validators.IDireccionValidator;
import com.isi.desa.Service.Interfaces.Validators.IHuespedValidator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//@Service //Descomentar para correr con Spring Boot
public class HuespedValidator implements IHuespedValidator {
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

        // 🔴 Cambio clave: CUIT opcional salvo Responsable Inscripto
        error = validateCuit(huespedDTO.cuit, huespedDTO.posicionIva); if (error != null) errores.add(error);

        if (huespedDTO.direccion != null) {
            errores.addAll(direccionValidator.validate(huespedDTO.direccion));
        } else {
            errores.add("La dirección es obligatoria");
        }
        return errores.isEmpty() ? null : errores;
    }

    public List<String> validateUpdate(HuespedDTO dto) {
        List<String> errores = new ArrayList<>();
        String e;

        if (dto.idHuesped != null) { e = validateIdHuesped(dto.idHuesped); if (e != null) errores.add(e); }
        if (dto.nombre != null)     { e = validateNombre(dto.nombre);      if (e != null) errores.add(e); }
        if (dto.apellido != null)   { e = validateApellido(dto.apellido);  if (e != null) errores.add(e); }
        if (dto.tipoDocumento != null) { e = validateTipoDocumento(dto.tipoDocumento); if (e != null) errores.add(e); }
        if (dto.numDoc != null)     { e = validateNumDoc(dto.numDoc);      if (e != null) errores.add(e); }

        // Si viene posición IVA o CUIT, aplicar la regla condicional
        if (dto.posicionIva != null || dto.cuit != null) {
            String pos = (dto.posicionIva != null) ? dto.posicionIva : "Consumidor Final";
            e = validateCuit(dto.cuit, pos); if (e != null) errores.add(e);
        }

        if (dto.direccion != null) {
            errores.addAll(direccionValidator.validate(dto.direccion));
        }
        return errores;
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
        if (tipodocumentoencontrado == null) {
            return "El tipo de documento ingresado no existe";
        }

        return null;
    }

    public String validateNumDoc(String numDoc) {
        return (numDoc == null || numDoc.trim().isEmpty()) ? "El número de documento es obligatorio" : null;
    }


    @Override
    public String validateCuit(String cuit) {
        // Compat: si no sabemos la posición IVA, tratamos CUIT como opcional,
        // pero si viene informado, validamos formato.
        if (cuit == null || cuit.trim().isEmpty()) return null;
        String regex = "\\d{2}-\\d{8}-\\d";
        if (!cuit.matches(regex)) return "El CUIT debe tener el formato XX-XXXXXXXX-X";
        return null;
    }

    // --- Nueva validación con IVA (regla del CU) ---
    public String validateCuit(String cuit, String posicionIva) {
        // 1) Responsable Inscripto → obligatorio con formato
        if (posicionIva != null && posicionIva.equalsIgnoreCase("Responsable Inscripto")) {
            if (cuit == null || cuit.trim().isEmpty())
                return "El CUIT es obligatorio para Responsable Inscripto";
            String regex = "\\d{2}-\\d{8}-\\d";
            if (!cuit.matches(regex))
                return "El CUIT debe tener el formato XX-XXXXXXXX-X";
            return null;
        }

        // 2) Resto (p.ej. Consumidor Final) → opcional, pero si viene debe validar formato
        if (cuit == null || cuit.trim().isEmpty()) return null;
        String regex = "\\d{2}-\\d{8}-\\d";
        if (!cuit.matches(regex)) return "El CUIT debe tener el formato XX-XXXXXXXX-X";
        return null;
    }

    public String validateFechaNacimiento(LocalDate fechaNacimiento) {
        return (fechaNacimiento == null) ? "La fecha de nacimiento es un campo obligatorio" : null;
    }



}
