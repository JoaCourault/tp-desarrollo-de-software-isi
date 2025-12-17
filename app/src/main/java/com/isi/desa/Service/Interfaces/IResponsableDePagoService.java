package com.isi.desa.Service.Interfaces;


import com.isi.desa.Dto.ResponsableDePago.*;

import java.util.List;

public interface IResponsableDePagoService {
    List<String> obtenerRazonesSocialesResponsablesDePago();
    BuscarResponsableDePagoResult BuscarResponsableDePago(BuscarResponsableDePagoRequest request);
    AltaResponsableDePagoResult AltaResponsableDePago(AltaResponsableDePagoRequest request);
    ModificarResponsableDePagoResult ModificarResponsableDePago(ModificarResponsableDePagoRequest request);
}
