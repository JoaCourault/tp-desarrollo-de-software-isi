package com.isi.desa.Dao.Repositories;

import com.isi.desa.Model.Entities.Estadia.Estadia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EstadiaRepository extends JpaRepository<Estadia, String> {

    // Buscamos estadías que se crucen con el rango solicitado
    @Query("SELECT e FROM Estadia e WHERE e.idHabitacion = :idHabitacion " +
            "AND e.checkIn <= :hasta AND e.checkOut >= :desde")
    List<Estadia> findEstadiasEnRango(@Param("idHabitacion") String idHabitacion,
                                      @Param("desde") LocalDateTime desde,
                                      @Param("hasta") LocalDateTime hasta);
}