package com.isi.desa.Model.Entities.NotaDeCredito;

import com.isi.desa.Model.Entities.Factura.Factura;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

@Entity
@Table(name = "NotaDeCredito")
public class NotaDeCredito {
    @Id
    @GeneratedValue(generator = "cod_identificador")
    @GenericGenerator(name = "cod_identificador", strategy = "uuid2")
    private String codigoIdentificador;
    @Column(name = "cobrado")
    private boolean cobrado;

    @OneToMany(mappedBy = "notaDeCredito")
    private List<Factura> facturas;   // muchas facturas pueden ser canceladas por la misma nota


    public NotaDeCredito() {}

    public NotaDeCredito(String codigoIdentificador, boolean cobrado, List<Factura> facturas) {
        this.codigoIdentificador = codigoIdentificador;
        this.cobrado = cobrado;
        this.facturas = facturas;
    }

    public String getCodigoIdentificador() { return codigoIdentificador; }
    public void setCodigoIdentificador(String codigoIdentificador) { this.codigoIdentificador = codigoIdentificador; }
    public boolean isCobrado() { return cobrado; }
    public void setCobrado(boolean cobrado) { this.cobrado = cobrado; }
    public List<Factura> getFacturas() { return facturas; }
    public void setFacturas(List<Factura> facturas) { this.facturas = facturas; }
}