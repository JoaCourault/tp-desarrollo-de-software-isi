package com.isi.desa.Model.Enums;

public enum EstadoReserva {
    REALIZADA,  // Reserva creada
    COMPLETADA,  // El huésped ya hizo Check-In
    CANCELADA,  // Se dio de baja antes de la fecha
    FINALIZADA,
    EFECTIVIZADA// El huésped ya se fue o nunca vino
}