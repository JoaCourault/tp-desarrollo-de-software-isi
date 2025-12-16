package com.isi.desa.Model.Enums;

public enum EstadoHabitacion {
    DISPONIBLE,
    RESERVADA,
    OCUPADA,
    FUERA_DE_SERVICIO
    // Nota: Si en tu código tenías "MANTENIMIENTO", cámbialo a "FUERA_DE_SERVICIO"
    // para que coincida con tu SQL: CHECK ... ANY (ARRAY['FUERA_DE_SERVICIO'...])
}