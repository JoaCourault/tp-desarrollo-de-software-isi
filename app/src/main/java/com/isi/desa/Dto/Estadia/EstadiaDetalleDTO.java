package com.isi.desa.Dto.Estadia;

import com.isi.desa.Dto.Factura.ItemFacturableDTO; // <--- Asegúrate que coincida con tu carpeta
import com.isi.desa.Dto.ResponsableDePago.PayerDTO;
import java.util.List;

public class EstadiaDetalleDTO {
    private String idEstadia;
    private String nroHabitacion;
    private List<ItemFacturableDTO> items;
    private List<PayerDTO> ocupantes;

    public EstadiaDetalleDTO() {}

    // Getters y Setters
    public String getIdEstadia() { return idEstadia; }
    public void setIdEstadia(String idEstadia) { this.idEstadia = idEstadia; }

    public String getNroHabitacion() { return nroHabitacion; }
    public void setNroHabitacion(String nroHabitacion) { this.nroHabitacion = nroHabitacion; }

    public List<ItemFacturableDTO> getItems() { return items; }
    public void setItems(List<ItemFacturableDTO> items) { this.items = items; }

    public List<PayerDTO> getOcupantes() { return ocupantes; }
    public void setOcupantes(List<PayerDTO> ocupantes) { this.ocupantes = ocupantes; }
}