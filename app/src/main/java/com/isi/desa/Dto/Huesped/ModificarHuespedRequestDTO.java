package com.isi.desa.Dto.Huesped;

public class ModificarHuespedRequestDTO {
    public HuespedDTO huesped;
    /** Si true, salta la advertencia de doc duplicado y aplica la modificación. */
    public Boolean aceptarIgualmente = false;
}
