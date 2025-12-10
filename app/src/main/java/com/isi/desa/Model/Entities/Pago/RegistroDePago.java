package com.isi.desa.Model.Entities.Pago;

import com.isi.desa.Model.Entities.MetodoDePago.MetodoDePago;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "RegistroDePago")
public class RegistroDePago {

    @Id
    @GeneratedValue(generator = "id_registro_pago")
    @GenericGenerator(name = "id_registro_pago", strategy = "uuid2")
    @Column(name = "id_registro_pago", nullable = false, updatable = false)
    private String id;

    @ManyToOne
    @JoinColumn(name = "id_pago", nullable = false)
    private Pago pago;

    @ManyToOne
    @JoinColumn(name = "id_metodo_pago", nullable = false)
    private MetodoDePago metodoDePago;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    @Column(name = "monto")
    private BigDecimal monto;
}
