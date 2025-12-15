package com.isi.desa.Dto.Huesped;

import com.isi.desa.Dto.Direccion.DireccionDTO;
import com.isi.desa.Dto.Estadia.EstadiaDTO;
import com.isi.desa.Dto.TipoDocumento.TipoDocumentoDTO;

import java.time.LocalDate;
import java.util.List;

public class HuespedDTO {
    public String idHuesped;
    public String nombre;
    public String apellido;
    public TipoDocumentoDTO tipoDocumento;
    public String numDoc;
    public String posicionIva;
    public String cuit;
    public LocalDate fechaNacimiento;
    public String telefono;
    public String email;
    public String ocupacion;
    public String nacionalidad;
    public DireccionDTO direccion;
    public List<EstadiaDTO> estadias;
    public boolean eliminado = false;
}
