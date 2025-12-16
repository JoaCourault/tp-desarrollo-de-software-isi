package com.isi.desa.Service.Interfaces.Validators;

import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Exceptions.Huesped.CannotCreateHuespedException;
import com.isi.desa.Exceptions.Huesped.CannotDeleteHuespedException;
import com.isi.desa.Exceptions.Huesped.CannotModifyHuespedEsception;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import java.util.List;

public interface IHuespedValidator {
    CannotCreateHuespedException validateCreate(HuespedDTO huespedDTO);
    CannotModifyHuespedEsception validateUpdate (HuespedDTO huespedDTO);
    CannotDeleteHuespedException validateDelete (String idHuesped);
    RuntimeException validateExists (String idHuesped);
}

