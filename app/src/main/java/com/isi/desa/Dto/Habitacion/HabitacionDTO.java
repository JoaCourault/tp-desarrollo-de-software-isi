package com.isi.desa.Dto.Habitacion;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.isi.desa.Model.Enums.EstadoHabitacion;

public class HabitacionDTO {

    @JsonProperty("id_habitacion")
    public String idHabitacion;
    public Float precio;
    public Integer numero;
    public Integer piso;
    public EstadoHabitacion estado;
    public Integer capacidad;
    public String detalles;
    public String tipoHabitacion;
    public Integer cantidadCamasIndividual;
    public Integer cantidadCamasDobles;
    public Integer cantidadCamasKingSize;
}