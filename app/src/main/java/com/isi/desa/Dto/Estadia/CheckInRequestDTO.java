package com.isi.desa.Dto.Estadia;

import com.isi.desa.Dto.Huesped.HuespedDTO;

import java.util.List;

public class CheckInRequestDTO {

    public HuespedDTO huespedTitular;

    public List<String> acompanantesIds;

    public List<HabitacionCheckInDTO> habitaciones;
}