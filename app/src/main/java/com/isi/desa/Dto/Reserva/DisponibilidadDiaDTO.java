package com.isi.desa.Dto.Reserva;

import java.time.LocalDate;

public class DisponibilidadDiaDTO {
    public LocalDate fecha;
    public String estado; // "DISPONIBLE", "OCUPADA", "RESERVADA", "MANTENIMIENTO"

    public DisponibilidadDiaDTO(LocalDate fecha, String estado) {
        this.fecha = fecha;
        this.estado = estado;
    }
}