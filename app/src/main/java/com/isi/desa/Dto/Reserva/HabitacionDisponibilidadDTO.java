package com.isi.desa.Dto.Reserva;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.isi.desa.Dto.Habitacion.HabitacionDTO;
import java.util.List;

public class HabitacionDisponibilidadDTO {
    public HabitacionDTO habitacion;
    public List<DisponibilidadDiaDTO> disponibilidad;

    public HabitacionDisponibilidadDTO(HabitacionDTO habitacion, List<DisponibilidadDiaDTO> disponibilidad) {
        this.habitacion = habitacion;
        this.disponibilidad = disponibilidad;
    }

    public HabitacionDisponibilidadDTO() {}

    public List<DisponibilidadDiaDTO> getDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(List<DisponibilidadDiaDTO> disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    public HabitacionDTO getHabitacion() {
        return habitacion;
    }

    public void setHabitacion(HabitacionDTO habitacion) {
        this.habitacion = habitacion;
    }
}