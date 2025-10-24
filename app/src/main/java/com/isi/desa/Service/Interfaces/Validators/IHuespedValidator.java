package com.isi.desa.Service.Interfaces.Validators;

import com.isi.desa.Dto.Huesped.HuespedDTO;
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
    String validateCuit(String cuit);
    default List<String> validateUpdate(HuespedDTO dto) {
        return java.util.Collections.emptyList();
    }
}

