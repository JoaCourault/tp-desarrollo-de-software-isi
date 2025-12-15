package com.isi.desa.Model.Entities.Direccion;

import com.fasterxml.jackson.annotation.JsonIgnore; // Importante para evitar bucles infinitos
import com.isi.desa.Model.Entities.Huesped.Huesped;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "direccion")
public class Direccion {

    @Id
    @Column(name = "id_direccion", nullable = false)
    private String idDireccion;

    // --- CAMBIO: Relación Bidireccional ---
    // 'mappedBy' indica que la clave foránea está en la clase 'Huesped', campo 'direccion'.
    // Esto permite hacer direccion.getHuesped() sin crear una columna circular en la BD.
    @OneToOne(mappedBy = "direccion")
    @JsonIgnore // Evita que al pedir la dirección, traiga al huésped, y este a la dirección... (bucle)
    private Huesped huesped;


    @Column(name = "calle") private String calle;
    @Column(name = "numero") private String numero;
    @Column(name = "depto") private String departamento;
    @Column(name = "piso") private Integer piso;
    @Column(name = "cp") private String cp;
    @Column(name = "localidad") private String localidad;
    @Column(name = "provincia") private String provincia;
    @Column(name = "pais") private String pais;

    public Direccion() {
        // ID automático si viene vacío
        this.idDireccion = "DIR-" + UUID.randomUUID().toString().substring(0, 8);
    }

    // Getters y Setters
    public String getIdDireccion() { return idDireccion; }
    public void setIdDireccion(String idDireccion) { this.idDireccion = idDireccion; }

    public Huesped getHuesped() { return huesped; }
    public void setHuesped(Huesped huesped) { this.huesped = huesped; }

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
}