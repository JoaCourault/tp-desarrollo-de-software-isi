package com.isi.desa.Service.Interfaces.Validators;

import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Exceptions.HuespedConEstadiaAsociadasException;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import java.util.List;

public interface IHuespedValidator {
    Huesped create(HuespedDTO huespedDTO);
    List<String> validateCreate(HuespedDTO huespedDTO);
    String validateIdHuesped(String idHuesped);
    String validateNombre(String nombre);
    String validateApellido(String apellido);
    String validateTipoDocumento(String tipoDocumento);
    String validateNumDoc(String numDoc);
    String validateCuit(Integer cuit);

    RuntimeException validateDelete (String idHuesped);
    RuntimeException validateExists (String idHuesped);
}

