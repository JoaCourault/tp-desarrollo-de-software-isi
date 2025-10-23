package com.isi.desa.Dto.Direccion;

import com.fasterxml.jackson.annotation.JsonAlias;

public class DireccionDTO {
    public String id;
    public String pais;
    public String provincia;
    public String localidad;
    @JsonAlias("cp")               // ← mapea "cp" a codigoPostal
    public Integer codigoPostal;
    public String calle;
    public Integer numero;
    public String departamento;
    public Integer piso;
}
