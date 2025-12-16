package com.isi.desa.Model.Entities.Huesped;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.isi.desa.Model.Entities.Direccion.Direccion;
import com.isi.desa.Model.Entities.Estadia.Estadia;
import com.isi.desa.Model.Entities.Tipodocumento.TipoDocumento;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "huesped")
public class Huesped {

    @Id
    @Column(name = "id_huesped", nullable = false)
    private String idHuesped;

    // --- RELACIÓN ONE-TO-ONE (Dirección) ---
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_direccion", referencedColumnName = "id_direccion", nullable = false)
    private Direccion direccion;

    // --- RELACIÓN ONE-TO-ONE (Tipo Documento) ---
    // CORRECCIÓN: Usamos @OneToOne según tu pedido
    @OneToOne
    @JoinColumn(name = "tipo_doc", nullable = false)
    private TipoDocumento tipoDoc;

    @Column(name = "num_doc", nullable = false) private String numDoc;
    @Column(name = "nombre") private String nombre;
    @Column(name = "apellido") private String apellido;
    @Column(name = "posicion_iva") private String posicionIva;
    @Column(name = "cuit") private String cuit;
    @Column(name = "fecha_nac") private LocalDate fechaNac;
    @Column(name = "telefono") private String telefono;
    @Column(name = "email") private String email;
    @Column(name = "ocupacion") private String ocupacion;
    @Column(name = "nacionalidad") private String nacionalidad;
    @Column(name = "eliminado") private boolean eliminado;

    @ManyToMany(mappedBy = "listaHuespedes")
    @JsonIgnore
    private List<Estadia> estadias = new ArrayList<>();

    public Huesped() {}

    // Helper de vinculación
    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
        if (direccion != null) {
            direccion.setHuesped(this);
        }
    }

    public Direccion getDireccion() { return direccion; }

    // Getters y Setters
    public String getIdHuesped() { return idHuesped; }
    public void setIdHuesped(String idHuesped) { this.idHuesped = idHuesped; }
    public String getNumDoc() { return numDoc; }
    public void setNumDoc(String numDoc) { this.numDoc = numDoc; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    // CORRECCIÓN: Getter y Setter trabajan con el Objeto TipoDocumento
    public TipoDocumento getTipoDocumento() { return tipoDoc; }
    public void setTipoDocumento(TipoDocumento tipoDoc) { this.tipoDoc = tipoDoc; }

    public String getPosicionIva() { return posicionIva; }
    public void setPosicionIva(String posicionIva) { this.posicionIva = posicionIva; }
    public String getCuit() { return cuit; }
    public void setCuit(String cuit) { this.cuit = cuit; }
    public LocalDate getFechaNac() { return fechaNac; }
    public void setFechaNac(LocalDate fechaNac) { this.fechaNac = fechaNac; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getOcupacion() { return ocupacion; }
    public void setOcupacion(String ocupacion) { this.ocupacion = ocupacion; }
    public String getNacionalidad() { return nacionalidad; }
    public void setNacionalidad(String nacionalidad) { this.nacionalidad = nacionalidad; }
    public boolean isEliminado() { return eliminado; }
    public void setEliminado(boolean eliminado) { this.eliminado = eliminado; }
    public List<Estadia> getEstadias() { return estadias; }
    public void setEstadias(List<Estadia> estadias) { this.estadias = estadias; }
}