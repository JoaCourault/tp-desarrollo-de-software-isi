package com.isi.desa.Model.Entities.Usuario;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @Column(name = "idusuario", nullable = false, length = 255)
    private String idUsuario;

    @Column(name = "contrasenia", nullable = false, length = 255)
    private String contrasenia;

    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 255)
    private String apellido;

    public Usuario() {}

    public Usuario(String idUsuario, String contrasenia, String nombre, String apellido) {
        this.idUsuario = idUsuario;
        this.contrasenia = contrasenia;
        this.nombre = nombre;
        this.apellido = apellido;
    }

    public String getIdUsuario() { return idUsuario; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }

    public String getContrasenia() { return contrasenia; }
    public void setContrasenia(String contrasenia) { this.contrasenia = contrasenia; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
}
