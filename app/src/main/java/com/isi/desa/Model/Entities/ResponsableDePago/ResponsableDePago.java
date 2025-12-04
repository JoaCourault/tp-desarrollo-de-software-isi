package com.isi.desa.Model.Entities.ResponsableDePago;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "ResponsableDePago")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class ResponsableDePago {
    @Id
    @GeneratedValue(generator = "id_responsable_pago")
    @GenericGenerator(name = "id_responsable_pago", strategy = "uuid2")
    private String idResponsableDePago;
    // Clase abstracta base para responsables de pago
}