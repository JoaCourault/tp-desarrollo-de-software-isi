package com.isi.desa.Dto.Reserva;

import java.util.List;

public class CrearReservaRequestDTO {
    // Nombres ajustados al Front
    public String nombreCliente;
    public String apellidoCliente;
    public String telefonoCliente;

    // Ahora recibe una lista de objetos
    public List<ReservaDetalleDTO> reservas;

    public CrearReservaRequestDTO() {}

    // Getters y Setters
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getApellidoCliente() { return apellidoCliente; }
    public void setApellidoCliente(String apellidoCliente) { this.apellidoCliente = apellidoCliente; }

    public String getTelefonoCliente() { return telefonoCliente; }
    public void setTelefonoCliente(String telefonoCliente) { this.telefonoCliente = telefonoCliente; }

    public List<ReservaDetalleDTO> getReservas() { return reservas; }
    public void setReservas(List<ReservaDetalleDTO> reservas) { this.reservas = reservas; }
}