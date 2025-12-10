package com.isi.desa.Model.Entities.Usuario;


import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "Usuario")
public class Usuario {

    @Id
    @GeneratedValue(generator = "id_usuario")
    @GenericGenerator(name = "id_usuario", strategy = "uuid2")
    @Column(name = "id_usuario", nullable = false, updatable = false)
    private String idUsuario;

    @Column(name = "contrasenia", nullable = false)
    private String contrasenia;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "apellido", nullable = false)
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
