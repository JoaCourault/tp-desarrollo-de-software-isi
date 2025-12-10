package com.isi.desa.Dto.Estadia;

import java.time.LocalDateTime;
import java.util.List;

public class HabitacionCheckInDTO {
    public String idHabitacion;
    public LocalDateTime fechaDesde;
    public LocalDateTime fechaHasta;
    public String idReservaAsociada;

    // NUEVO: Lista de IDs de las personas en esta habitación
    public List<String> acompanantesIds;
}