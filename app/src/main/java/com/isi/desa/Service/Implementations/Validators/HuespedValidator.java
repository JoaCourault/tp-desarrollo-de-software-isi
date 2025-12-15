package com.isi.desa.Service.Implementations.Validators;

import com.isi.desa.Dao.Interfaces.IHuespedDAO;
import com.isi.desa.Dao.Implementations.TipoDocumentoDAO; // O idealmente ITipoDocumentoDAO si tenes la interfaz
import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Dto.TipoDocumento.TipoDocumentoDTO;
import com.isi.desa.Exceptions.Direccion.InvalidDirectionException;
import com.isi.desa.Exceptions.Huesped.*;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Model.Entities.Tipodocumento.TipoDocumento;
import com.isi.desa.Service.Interfaces.Validators.IDireccionValidator;
import com.isi.desa.Service.Interfaces.Validators.IHuespedValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component; // Usamos Component o Service

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class HuespedValidator implements IHuespedValidator {

    @Autowired
    private IDireccionValidator direccionValidator;

    @Autowired
    private IHuespedDAO dao;

    // Asumo que tienes este DAO como Bean de Spring también.
    // Si no tienes interfaz ITipoDocumentoDAO, usa la clase concreta, pero inyectada.
    @Autowired
    private TipoDocumentoDAO tipoDocumentoDAO;

    // ---------------------------------------------------------
    // ¡ELIMINADO! : create(), INSTANCE, getInstance() y constructor privado.
    // Ahora Spring maneja todo.
    // ---------------------------------------------------------

    @Override
    public CannotCreateHuespedException validateCreate(HuespedDTO huespedDTO) {
        List<String> errores = new ArrayList<>(); // Usamos lista de Strings para simplificar

        if (huespedDTO == null) return new CannotCreateHuespedException("El DTO es nulo");

        if (isBlank(huespedDTO.nombre)) errores.add("El nombre es obligatorio");
        if (isBlank(huespedDTO.apellido)) errores.add("El apellido es obligatorio");
        if (isBlank(huespedDTO.numDoc)) errores.add("El numero de documento es obligatorio");

        String tipoDocumentoError = validateTipoDocumento(huespedDTO.tipoDoc);
        if (tipoDocumentoError != null) errores.add(tipoDocumentoError);

        if(huespedDTO.cuit != null) {
            String cuitError = validateCuit(huespedDTO.cuit);
            if (cuitError != null) errores.add(cuitError);
        }

        String fechaNacimientoError = validateFechaNacimiento(huespedDTO.fechaNac);
        if (fechaNacimientoError != null) errores.add(fechaNacimientoError);

        // Validación de dirección delegada
        if (huespedDTO.direccion != null) {
            try {
                InvalidDirectionException dirError = direccionValidator.validate(huespedDTO.direccion);
                if (dirError != null) errores.add(dirError.getMessage());
            } catch (Exception e) {
                errores.add("Error validando dirección: " + e.getMessage());
            }
        }

        if (!errores.isEmpty()) {
            // Unimos todos los errores en un solo mensaje
            String mensajeFinal = String.join("; ", errores);
            return new CannotCreateHuespedException(mensajeFinal);
        }

        return null;
    }

    public CannotDeleteHuespedException validateDelete(String idHuesped) {

        // 1. Validar formato ID (Reutilizamos validateExists pero capturamos el error)
        CannotDeleteHuespedException errorFormato = (CannotDeleteHuespedException) this.validateExists(idHuesped);
        if(errorFormato != null) {
            return new CannotDeleteHuespedException(errorFormato.getMessage());
        }

        // 2. Validar existencia real en BD
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
        List<String> errores = new ArrayList<>();

        if (dto == null) {
            return new CannotModifyHuespedEsception("No se han proporcionado datos del huesped a modificar.");
        }

        if (isBlank(dto.idHuesped)) {
            errores.add("El ID del huesped es obligatorio para modificar.");
        }

        // Validamos Tipo Documento
        String tipoDocumentoError = validateTipoDocumento(dto.tipoDoc);
        if (tipoDocumentoError != null) errores.add(tipoDocumentoError);

        // Validamos CUIT
        if(!isBlank(dto.cuit)) {
            String cuitError = validateCuit(dto.cuit);
            if (cuitError != null) errores.add(cuitError);
        }

        if (!errores.isEmpty()) {
            String mensajeFinal = String.join("; ", errores);
            return new CannotModifyHuespedEsception(mensajeFinal);
        }

        return null;
    }

    @Override
    public RuntimeException validateExists(String idHuesped) {
        if (isBlank(idHuesped)) {
            return new IllegalArgumentException("El idHuesped es obligatorio para verificar existencia");
        }
        // Nota: Aquí solo validamos formato o nulidad.
        // La validación contra BD se suele hacer dentro del método específico (delete/update)
        // para evitar doble consulta si no es necesario.
        return null;
    }

    // --- MÉTODOS PRIVADOS AUXILIARES ---

    private String validateTipoDocumento(TipoDocumentoDTO tipoDocumentoDTO) {
        // Usamos el DAO inyectado, NO hacemos "new TipoDocumentoDAO()"
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
            // Regex básico, puedes mejorarlo
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