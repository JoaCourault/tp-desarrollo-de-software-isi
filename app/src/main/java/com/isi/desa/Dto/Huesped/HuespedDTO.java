package com.isi.desa.Dto.Huesped;

import com.isi.desa.Dto.Direccion.DireccionDTO;

import java.time.LocalDate;

public class HuespedDTO {
    public String idHuesped;
    public String nombre;
    public String apellido;
    public String tipoDocumento;
    public String numDoc;
    public String posicionIva;
    public String cuit;
    public LocalDate fechaNacimiento;
    public String telefono;
    public String email;
    public String ocupacion;
    public String nacionalidad;
    public DireccionDTO direccion;
}
