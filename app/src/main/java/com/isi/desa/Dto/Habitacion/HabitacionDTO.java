package com.isi.desa.Dto.Habitacion;

import com.isi.desa.Model.Enums.EstadoHabitacion;
import java.math.BigDecimal;

public class HabitacionDTO {
    public String idHabitacion;
    public BigDecimal precio; // BigDecimal
    public Integer numero;
    public Integer piso;
    public Integer capacidad;
    public String detalles;
    public EstadoHabitacion estado;

    // Este campo es CRÍTICO para saber qué clase hija crear
    public String tipoHabitacion;

    public Integer qCamDobles;
    public Integer qCamIndividual;
    public Integer qCamKingSize;
}