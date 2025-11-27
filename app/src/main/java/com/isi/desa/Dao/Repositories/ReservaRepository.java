package com.isi.desa.Dao.Repositories;

import com.isi.desa.Model.Entities.Reserva.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, String> {

    @Query("SELECT r FROM Reserva r WHERE r.habitacion.idHabitacion = :idHabitacion " +
            "AND r.fechaDesde <= :hasta AND r.fechaHasta >= :desde")
    List<Reserva> findReservasEnRango(
            @Param("idHabitacion") String idHabitacion,
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta);

    List<Reserva> findByHabitacion_IdHabitacion(String idHabitacion);
}
