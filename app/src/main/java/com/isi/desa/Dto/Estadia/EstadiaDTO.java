package com.isi.desa.Dto.Estadia;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class EstadiaDTO {
    public String idEstadia;
    public Float valorTotalEstadia;
    public LocalDateTime checkIn;
    public LocalDateTime checkOut;
    public Integer cantNoches;
    public String idFactura;
    public String idReserva;
    public String idHuespedTitular; // El que paga/responsable
    public List<String> idsOcupantes;
    public List<String> idsHabitaciones;
}