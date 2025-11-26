package com.isi.desa.Service.Implementations.Validators;

import com.isi.desa.Dao.Interfaces.IHuespedDAO;
import com.isi.desa.Dao.Interfaces.ITipoDocumentoDAO;
import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Dto.TipoDocumento.TipoDocumentoDTO;
import com.isi.desa.Exceptions.Direccion.InvalidDirectionException;
import com.isi.desa.Exceptions.Huesped.*;
import com.isi.desa.Model.Entities.Direccion.Direccion;
import com.isi.desa.Model.Entities.Huesped.Huesped;
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

@Service("huespedValidator")
public class HuespedValidator implements IHuespedValidator {

    @Autowired
    private IDireccionValidator direccionValidator;

    @Qualifier("huespedDAO")
    @Autowired
    private IHuespedDAO dao;

    @Autowired
    private ITipoDocumentoDAO tipoDocumentoDAO;

    @Override
    public Huesped create(HuespedDTO dto) {

        CannotCreateHuespedException err = validateCreate(dto);
        if (err != null) throw err;

        Direccion direccion = DireccionMapper.dtoToEntity(dto.direccion);
        TipoDocumento tipoDocumento = new TipoDocumento(dto.tipoDocumento.tipoDocumento);

        // ✅ Crear huésped usando setters (constructor vacío)
        Huesped h = new Huesped();
        h.setIdHuesped(dto.idHuesped);
        h.setNombre(dto.nombre);
        h.setApellido(dto.apellido);
        h.setTipoDocumento(tipoDocumento.getTipoDocumento());
        h.setNumDoc(dto.numDoc);
        h.setPosicionIva(dto.posicionIva);
        h.setCuit(dto.cuit);
        h.setFechaNac(dto.fechaNacimiento);
        h.setTelefono(dto.telefono);
        h.setEmail(dto.email);
        h.setOcupacion(dto.ocupacion);
        h.setNacionalidad(dto.nacionalidad);
        h.setEliminado(false);

        return h;
    }

    @Override
    public CannotCreateHuespedException validateCreate(HuespedDTO dto) {

        List<RuntimeException> errores = new ArrayList<>();

        if (isBlank(dto.nombre)) errores.add(new IllegalArgumentException("El nombre es obligatorio"));
        if (isBlank(dto.apellido)) errores.add(new IllegalArgumentException("El apellido es obligatorio"));
        if (isBlank(dto.numDoc)) errores.add(new IllegalArgumentException("El número de documento es obligatorio"));

        String errorTipoDoc = validateTipoDocumento(dto.tipoDocumento);
        if (errorTipoDoc != null) errores.add(new IllegalArgumentException(errorTipoDoc));

        if (!isBlank(dto.cuit)) {
            String cuitError = validateCuit(dto.cuit);
            if (cuitError != null) errores.add(new IllegalArgumentException(cuitError));
        }

        String errorFN = validateFechaNacimiento(dto.fechaNacimiento);
        if (errorFN != null) errores.add(new IllegalArgumentException(errorFN));

        InvalidDirectionException dirError = direccionValidator.validate(dto.direccion);
        if (dirError != null) errores.add(dirError);

        if (!errores.isEmpty()) {
            return new CannotCreateHuespedException(
                    errores.stream()
                            .map(Throwable::getMessage)
                            .reduce((a, b) -> a + "; " + b)
                            .orElse("")
            );
        }

        return null;
    }

    @Override
    public CannotModifyHuespedEsception validateUpdate(HuespedDTO dto) {

        List<RuntimeException> errores = new ArrayList<>();

        if (dto == null)
            return new CannotModifyHuespedEsception("No se enviaron datos para modificar");

        String errorTipoDoc = validateTipoDocumento(dto.tipoDocumento);
        if (errorTipoDoc != null) errores.add(new IllegalArgumentException(errorTipoDoc));

        if (!isBlank(dto.cuit)) {
            String cuitError = validateCuit(dto.cuit);
            if (cuitError != null) errores.add(new IllegalArgumentException(cuitError));
        }

        if (!errores.isEmpty()) {
            return new CannotModifyHuespedEsception(
                    errores.stream()
                            .map(Throwable::getMessage)
                            .reduce((a, b) -> a + "; " + b)
                            .orElse("")
            );
        }

        return null;
    }

    @Override
    public CannotDeleteHuespedException validateDelete(String idHuesped) {

        RuntimeException existe = validateExists(idHuesped);
        if (existe != null)
            return new CannotDeleteHuespedException(existe.getMessage());

        if (!dao.obtenerEstadiasDeHuesped(idHuesped).isEmpty()) {
            return new CannotDeleteHuespedException(
                    "No se puede eliminar el huésped porque tiene estadías asociadas."
            );
        }

        return null;
    }

    @Override
    public RuntimeException validateExists(String idHuesped) {
        if (isBlank(idHuesped))
            return new IllegalArgumentException("El idHuesped es obligatorio");

        try {
            dao.getById(idHuesped);
        } catch (Exception e) {
            return new HuespedNotFoundException("No existe un huésped con ese ID");
        }

        return null;
    }

    private String validateTipoDocumento(TipoDocumentoDTO tipoDocumentoDTO) {

        if (tipoDocumentoDTO == null)
            return "El tipo de documento es obligatorio";

        String tipo = tipoDocumentoDTO.tipoDocumento;

        if (isBlank(tipo))
            return "El nombre del tipo de documento es obligatorio";

        try {
            tipoDocumentoDAO.obtener(tipo);
        } catch (Exception e) {
            return "El tipo de documento ingresado no existe";
        }

        return null;
    }

    private String validateCuit(String cuit) {
        if (!isBlank(cuit)) {
            if (!cuit.matches("\\d{2}-\\d{8}-\\d{1}")) {
                return "El CUIT debe tener formato XX-XXXXXXXX-X";
            }
        }
        return null;
    }

    private String validateFechaNacimiento(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null)
            return "La fecha de nacimiento es obligatoria";

        if (fechaNacimiento.isAfter(LocalDate.now()))
            return "La fecha de nacimiento no puede ser futura";

        return null;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
