package com.isi.desa.Dto.Reserva;

import java.util.List;

public class CrearReservaRequestDTO {
    public String nombreCliente;
    public String apellidoCliente;
    public String telefonoCliente;

    public List<ReservaDetalleDTO> reservas;
}