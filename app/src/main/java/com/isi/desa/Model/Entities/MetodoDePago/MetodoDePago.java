package com.isi.desa.Model.Entities.MetodoDePago;

import com.isi.desa.Model.Entities.Pago.RegistroDePago;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

@Entity
@Table(name = "MetodoDePago")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class MetodoDePago {
    @Id
    @GeneratedValue(generator = "idMetodoDePago")
    @GenericGenerator(name = "idMetodoDePago", strategy = "uuid2")
    @Column(name = "idMetodoDePago", nullable = false, updatable = false)
    private String idMetodoDePago;

    @OneToMany(mappedBy = "metodoDePago")
    private List<RegistroDePago> registros;

    // Clase abstracta base para metodos de pago

    public String getIdMetodoDePago() { return idMetodoDePago; }
    public void setIdMetodoDePago(String idMetodoDePago) { this.idMetodoDePago = idMetodoDePago; }
}