package com.isi.desa.Model.Entities.Huesped;

import com.isi.desa.Model.Entities.Direccion.Direccion;
import com.isi.desa.Model.Entities.Estadia.Estadia;
import com.isi.desa.Model.Entities.Tipodocumento.TipoDocumento;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Huesped")
public class Huesped {
    @Id
    @GeneratedValue(generator = "id_huesped")
    @GenericGenerator(name = "id_huesped", strategy = "uuid2")
    @Column(name = "id_huesped", nullable = false, updatable = false)
    private String idHuesped;
    @Column(name = "num_doc", nullable = false)
    private String numDoc;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "apellido")
    private String apellido;
    @ManyToOne
    @JoinColumn(name = "tipo_doc", referencedColumnName = "tipoDocumento", nullable = false)
    private TipoDocumento tipoDoc;
    @Column(name = "posicion_iva")
    private String posicionIva;
    @Column(name = "cuit")
    private String cuit;
    @Column(name = "fecha_nac")
    private LocalDate fechaNac;
    @Column(name = "telefono")
    private String telefono;
    @Column(name = "email")
    private String email;
    @Column(name = "ocupacion")
    private String ocupacion;
    @Column(name = "nacionalidad")
    private String nacionalidad;
    @Column(name = "eliminado")
    private boolean eliminado;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_direccion", referencedColumnName = "id_direccion")
    private Direccion direccion;

    @ManyToMany(mappedBy = "huespedesHospedados")
    private List<Estadia> estadiasDelHuesped;

    public Huesped() {}

    public Huesped(String nombre, String apellido, TipoDocumento tipoDocumento, String numDoc, String posicionIva, String cuit,
                   LocalDate fechaNacimiento, String telefono, String email, String ocupacion, String nacionalidad,
                   Direccion direccion, String idHuesped) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.tipoDoc = tipoDocumento;
        this.numDoc = numDoc;
        this.posicionIva = posicionIva;
        this.cuit = cuit;
        this.fechaNac = fechaNacimiento;
        this.telefono = telefono;
        this.email = email;
        this.ocupacion = ocupacion;
        this.nacionalidad = nacionalidad;
        this.direccion = direccion;
        this.idHuesped = idHuesped;
    }
    // Getters y setters
    public String getIdHuesped() { return idHuesped; }
    public void setIdHuesped(String idHuesped) { this.idHuesped = idHuesped; }

    public String getNumDoc() { return numDoc; }
    public void setNumDoc(String numDoc) { this.numDoc = numDoc; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public TipoDocumento getTipoDoc() { return tipoDoc; }
    public void setTipoDoc(TipoDocumento tipoDoc) { this.tipoDoc = tipoDoc; }

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

    public Direccion getDireccion() { return direccion; }
    public void setDireccion(Direccion direccion) { this.direccion = direccion; }

    public int getEdad() {
        if (this.fechaNac == null) {
            return 0;
        }
        return LocalDate.now().getYear() - this.fechaNac.getYear();
    }
}
