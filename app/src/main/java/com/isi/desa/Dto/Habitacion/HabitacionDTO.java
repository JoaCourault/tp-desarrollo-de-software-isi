package com.isi.desa.Dto.Habitacion;

import com.isi.desa.Model.Enums.EstadoHabitacion;
import com.isi.desa.Model.Enums.TipoHabitacion;

import java.math.BigDecimal;

public class HabitacionDTO {
    public String idHabitacion;
    public BigDecimal precio;
    public Integer numero;
    public Integer piso;
    public EstadoHabitacion estado;
    public Integer capacidad;
    public String detalles;
    public TipoHabitacion tipoHabitacion;

    public Integer cantidadCamasDobles;
    public Integer cantidadCamasKingSize;
}
