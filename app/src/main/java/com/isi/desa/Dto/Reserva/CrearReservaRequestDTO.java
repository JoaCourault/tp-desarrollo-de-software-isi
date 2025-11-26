package com.isi.desa.Dto.Reserva;
import java.time.LocalDate;
import java.util.List;

public class CrearReservaRequestDTO {
    public String nombreCliente;
    public String apellidoCliente;
    public String telefonoCliente;
    public LocalDate fechaIngreso;
    public LocalDate fechaEgreso;
    public List<String> idsHabitaciones; // IDs de las habitaciones a reservar
}