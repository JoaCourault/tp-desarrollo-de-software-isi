package com.isi.desa.Dto.Reserva;

import java.time.LocalDate;

public class DisponibilidadDiaDTO {
    public LocalDate fecha;
    public String estado; // "DISPONIBLE", "OCUPADA", "RESERVADA", "MANTENIMIENTO"
    private String idReserva;
    private boolean esSalida;
    private String tipoSalida;

    public DisponibilidadDiaDTO(LocalDate fecha, String estado) {
        this.fecha = fecha;
        this.estado = estado;
        this.esSalida = false;
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
    public String getIdReserva() { return idReserva; }
    public void setIdReserva(String idReserva) { this.idReserva = idReserva; }
    public boolean isEsSalida() { return esSalida; }
    public void setEsSalida(boolean esSalida) { this.esSalida = esSalida; }
    public String getTipoSalida() { return tipoSalida; }
    public void setTipoSalida(String tipoSalida) { this.tipoSalida = tipoSalida; }
}