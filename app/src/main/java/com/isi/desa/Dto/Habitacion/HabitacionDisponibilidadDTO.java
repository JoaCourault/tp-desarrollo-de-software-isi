package com.isi.desa.Dto.Habitacion; // <--- Cambio de paquete

import java.util.List;

public class HabitacionDisponibilidadDTO {
    // Ya no necesitas importar HabitacionDTO porque están en el mismo paquete
    public HabitacionDTO habitacion;
    public List<DisponibilidadDiaDTO> disponibilidad;
}