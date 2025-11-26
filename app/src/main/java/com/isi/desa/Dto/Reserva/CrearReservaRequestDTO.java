package com.isi.desa.Dto.Reserva;

import java.time.LocalDate;
import java.util.List;

public class CrearReservaRequestDTO {
    public String nombreCliente;
    public String apellidoCliente;
    public String telefonoCliente;

    // Lista de reservas individuales
    public List<ReservaHabitacionDTO> reservas;
}
