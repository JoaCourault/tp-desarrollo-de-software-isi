package com.isi.desa.Dao.Repositories;

import com.isi.desa.Model.Entities.Reserva.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, String> {

    // Busca reservas que se solapen con el rango [desde, hasta]
    @Query("SELECT r FROM Reserva r WHERE " +
            "r.fechaIngreso <= :hasta AND r.fechaEgreso >= :desde")
    List<Reserva> findReservasEnRango(@Param("desde") LocalDate desde,
                                      @Param("hasta") LocalDate hasta);
}