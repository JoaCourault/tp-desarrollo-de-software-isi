package com.isi.desa.Dto.Estadia;

import java.time.LocalDate; // <--- CAMBIAR A LOCALDATE
import java.util.List;

public class CrearEstadiaRequestDTO {
    // Recibimos solo fecha del front para evitar líos de formato
    private LocalDate checkIn;
    private LocalDate checkOut;

    private Integer cantNoches;
    private String idReserva;

    private List<String> idsHabitaciones;
    private List<String> idsHuespedes;

    public CrearEstadiaRequestDTO() {}

    // Getters y Setters (Asegúrate de cambiar los tipos aquí también)
    public LocalDate getCheckIn() { return checkIn; }
    public void setCheckIn(LocalDate checkIn) { this.checkIn = checkIn; }

    public LocalDate getCheckOut() { return checkOut; }
    public void setCheckOut(LocalDate checkOut) { this.checkOut = checkOut; }

    // ... resto igual ...
    public Integer getCantNoches() { return cantNoches; }
    public void setCantNoches(Integer cantNoches) { this.cantNoches = cantNoches; }
    public String getIdReserva() { return idReserva; }
    public void setIdReserva(String idReserva) { this.idReserva = idReserva; }
    public List<String> getIdsHabitaciones() { return idsHabitaciones; }
    public void setIdsHabitaciones(List<String> idsHabitaciones) { this.idsHabitaciones = idsHabitaciones; }
    public List<String> getIdsHuespedes() { return idsHuespedes; }
    public void setIdsHuespedes(List<String> idsHuespedes) { this.idsHuespedes = idsHuespedes; }
}