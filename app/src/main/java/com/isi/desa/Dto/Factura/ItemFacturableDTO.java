package com.isi.desa.Dto.Factura;

import java.math.BigDecimal;

public class ItemFacturableDTO {
    private String id;          // ID del servicio o de la estadía
    private String descripcion;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private boolean seleccionado; // Para que el front lo marque por defecto

    public ItemFacturableDTO() {}

    public ItemFacturableDTO(String id, String descripcion, Integer cantidad, BigDecimal precioUnitario) {
        this.id = id;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.seleccionado = true;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    public boolean isSeleccionado() { return seleccionado; }
    public void setSeleccionado(boolean seleccionado) { this.seleccionado = seleccionado; }
}