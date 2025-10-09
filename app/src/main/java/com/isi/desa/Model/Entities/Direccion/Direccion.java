package com.isi.desa.Model.Entities.Direccion;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Direccion {
    @JsonProperty("id")
    private String idDireccion;

    private String calle;
    private Integer numero;
    private String departamento;
    private Integer piso;
    private Integer cp;
    private String localidad;
    private String provincia;
    private String pais;

    public Direccion() {}

    public Direccion(String idDireccion) {
        this.idDireccion = idDireccion;
    }

    public Direccion(String id,String calle, Integer numero, String departamento, Integer piso, Integer cp, String localidad, String provincia, String pais) {
        this.idDireccion = id;
        this.calle = calle;
        this.numero = numero;
        this.departamento = departamento;
        this.piso = piso;
        this.cp = cp;
        this.localidad = localidad;
        this.provincia = provincia;
        this.pais = pais;
    }

    public String getCalle() { return calle; }
    public void setCalle(String calle) { this.calle = calle; }

    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public Integer getPiso() { return piso; }
    public void setPiso(Integer piso) { this.piso = piso; }

    public Integer getCp() { return cp; }
    public void setCp(Integer cp) { this.cp = cp; }

    public String getLocalidad() { return localidad; }
    public void setLocalidad(String localidad) { this.localidad = localidad; }

    public String getProvincia() { return provincia; }
    public void setProvincia(String provincia) { this.provincia = provincia; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public String getIdDireccion() {
        return idDireccion;
    }

    public void setIdDireccion(String idDireccion) {
        this.idDireccion = idDireccion;
    }
}
