package com.isi.desa.Dao.Repositories;

import com.isi.desa.Model.Entities.Estadia.Estadia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

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
}
