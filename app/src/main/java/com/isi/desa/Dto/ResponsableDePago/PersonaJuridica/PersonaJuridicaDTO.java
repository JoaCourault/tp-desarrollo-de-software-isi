package com.isi.desa.Dto.ResponsableDePago.PersonaJuridica;

import com.isi.desa.Dto.Direccion.DireccionDTO;
import com.isi.desa.Dto.ResponsableDePago.ResponsableDePagoDTO;

public class PersonaJuridicaDTO extends ResponsableDePagoDTO {
    public String cuit;
    public String telefono;
    public String razonSocial;
    public DireccionDTO direccion;
}
