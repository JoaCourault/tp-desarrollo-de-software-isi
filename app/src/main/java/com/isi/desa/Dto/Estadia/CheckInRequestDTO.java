package com.isi.desa.Dto.Estadia;

import java.util.List;

public class CheckInRequestDTO {
    // CAMBIO: Ahora recibimos solo el ID (String), no el objeto entero.
    public String idHuespedTitular;

    public List<HabitacionCheckInDTO> habitaciones;
}