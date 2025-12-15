package com.isi.desa.Dao.Repositories;

import com.isi.desa.Model.Entities.Reserva.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, String> {
    // Busca por apellido (obligatorio) y nombre (opcional).
    // LOWER para hacerla insensible a mayúsculas/minúsculas.
    @Query("SELECT r FROM Reserva r " +
            "WHERE LOWER(r.apellidoHuesped) LIKE LOWER(CONCAT(:apellido, '%')) " +
            "AND (:nombre IS NULL OR LOWER(r.nombreHuesped) LIKE LOWER(CONCAT(:nombre, '%')))")
    List<Reserva> buscarPorHuesped(@Param("apellido") String apellido,
                                   @Param("nombre") String nombre);
    // Consulta corregida para comparar fechas con horas
    @Query("SELECT r FROM Reserva r WHERE r.fechaIngreso < :hasta AND r.fechaEgreso > :desde")
    List<Reserva> findReservasEnRango(@Param("desde") LocalDateTime desde,
                                      @Param("hasta") LocalDateTime hasta);

    @Query("SELECT r FROM Reserva r WHERE r.habitacion.idHabitacion = :idHabitacion " +
            "AND :fecha >= r.fechaIngreso AND :fecha < r.fechaEgreso")

    Optional<Reserva> findReservaActivaPorHabitacion(@Param("idHabitacion") String idHabitacion,
                                                     @Param("fecha") LocalDate fecha);

    Optional<Reserva> findTopByOrderByIdReservaDesc();
}