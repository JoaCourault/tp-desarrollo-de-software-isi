package com.isi.desa.Dao.Repositories.Projections;
import java.math.BigDecimal;

public interface HabitacionResumen {
    String getIdHabitacion();
    Integer getNumero();
    BigDecimal getPrecio();
    String getDetalles();
    Integer getCapacidad();
    String getEstado();
    String getTipoHabitacionStr();
}