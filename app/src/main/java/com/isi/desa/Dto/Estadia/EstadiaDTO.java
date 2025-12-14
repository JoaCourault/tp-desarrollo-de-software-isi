package com.isi.desa.Dto.Estadia;

import com.isi.desa.Dto.Habitacion.HabitacionDTO;
import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Dto.Reserva.ReservaDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class EstadiaDTO {
    public String idEstadia;
    public BigDecimal valorTotalEstadia;
    public LocalDateTime checkIn;
    public LocalDateTime checkOut;
    public Integer cantNoches;
    public ReservaDTO reserva;
    public List<HabitacionDTO> habitaciones;
    public List<HuespedDTO> huespedesHospedados;
}
