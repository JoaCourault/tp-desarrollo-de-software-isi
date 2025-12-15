package com.isi.desa.Dto.Reserva;

import java.time.LocalDate;

public class DisponibilidadDiaDTO {
    public LocalDate fecha;
    public String estado; // "DISPONIBLE", "OCUPADA", "RESERVADA", "MANTENIMIENTO"

    public DisponibilidadDiaDTO(LocalDate fecha, String estado) {
        this.fecha = fecha;
        this.estado = estado;
    }

    public DisponibilidadDiaDTO() {}

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}