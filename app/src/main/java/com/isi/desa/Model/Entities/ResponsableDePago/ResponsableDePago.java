package com.isi.desa.Model.Entities.ResponsableDePago;

import com.isi.desa.Model.Entities.Factura.Factura;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

@Entity
@Table(name = "ResponsableDePago")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class ResponsableDePago {
    @Id
    @GeneratedValue(generator = "id_responsable_pago")
    @GenericGenerator(name = "id_responsable_pago", strategy = "uuid2")
    @Column(name = "id_responsable_de_pago", updatable = false, nullable = false)
    private String idResponsableDePago;
    @OneToMany(mappedBy = "responsableDePago")
    private List<Factura> facturas;

    // Clase abstracta base para responsables de pago

    public String getIdResponsableDePago() {
        return idResponsableDePago;
    }

    public void setIdResponsableDePago(String idResponsableDePago) {
        this.idResponsableDePago = idResponsableDePago;
    }
}