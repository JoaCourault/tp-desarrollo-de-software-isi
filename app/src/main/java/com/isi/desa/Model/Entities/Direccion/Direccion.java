package com.isi.desa.Model.Entities.Direccion;

import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Model.Entities.ResponsableDePago.PersonaJuridica;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "Direccion")
public class Direccion {
    @Id
    @GeneratedValue(generator = "id_direccion")
    @GenericGenerator(name = "id_direccion", strategy = "uuid2")
    @Column(name = "id_direccion", updatable = false, nullable = false)
    private String idDireccion;
    @Column(name = "calle")
    private String calle;
    @Column(name = "numero")
    private String numero;
    @Column(name = "depto")
    private String departamento;
    @Column(name = "piso")
    private Integer piso;
    @Column(name = "cp")
    private String cp;
    @Column(name = "localidad")
    private String localidad;
    @Column(name = "provincia")
    private String provincia;
    @Column(name = "pais")
    private String pais;

    public Direccion() {}

    public Direccion(String id,String calle, String numero, String departamento, Integer piso, String cp, String localidad, String provincia, String pais) {
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

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public Integer getPiso() { return piso; }
    public void setPiso(Integer piso) { this.piso = piso; }

    public String getCp() { return cp; }
    public void setCp(String cp) { this.cp = cp; }

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
