package com.isi.desa.Dao.Repositories;

import com.isi.desa.Model.Entities.Estadia.Estadia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EstadiaRepository extends JpaRepository<Estadia, String> {
    @Query("""
        SELECT e
        FROM Estadia e
        JOIN e.habitaciones h
        WHERE h.idHabitacion = :idHabitacion
          AND :moment >= e.checkIn
          AND (e.checkOut IS NULL OR e.checkOut <= :moment)
    """)
    List<Estadia> findByHabitacionAndMoment(
            @Param("idHabitacion") String idHabitacion,
            @Param("moment") LocalDateTime moment
    );
    @Query("SELECT e FROM Estadia e WHERE e.checkIn <= :hasta AND e.checkOut >= :desde")
    List<Estadia> findEstadiasEnRango(@Param("desde") LocalDateTime desde,
                                      @Param("hasta") LocalDateTime hasta);
    @Query("SELECT e FROM Estadia e JOIN e.habitaciones h WHERE " +
            "h.idHabitacion = :idHabitacion AND " +
            "(e.checkIn < :hasta AND e.checkOut > :desde)")
    List<Estadia> findEstadiasPorHabitacionYFecha(
            @Param("idHabitacion") String idHabitacion,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
    );

    Optional<Estadia> findTopByOrderByIdEstadiaDesc();
}
