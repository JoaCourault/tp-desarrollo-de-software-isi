package com.isi.desa.Dto.Factura;

import java.math.BigDecimal;
import java.util.List;

public class GenerarFacturaRequestDTO {
    public String idEstadia;
    public String idResponsable; // ID del ResponsableDePago (o del Huésped)
    public String tipoFactura;   // "A", "B"
    public BigDecimal total;
    public List<ItemRequestDTO> items;

    // Clase interna para los items
    public static class ItemRequestDTO {
        public String idServicio; // Puede ser null si es alojamiento
        public String descripcion;
        public Integer cantidad;
        public BigDecimal monto;
    }
}