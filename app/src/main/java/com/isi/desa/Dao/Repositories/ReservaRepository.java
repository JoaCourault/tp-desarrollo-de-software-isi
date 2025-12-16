package com.isi.desa.Dao.Repositories;

import com.isi.desa.Model.Entities.Reserva.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, String> {

    @Query("""
        SELECT r
        FROM Reserva r
        WHERE r.habitacion.idHabitacion = :idHabitacion
          AND r.fechaIngreso <= :hasta
          AND r.fechaEgreso  >= :desde
    """)
    List<Reserva> findReservasEnRango(
            @Param("idHabitacion") String idHabitacion,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
    );
}
