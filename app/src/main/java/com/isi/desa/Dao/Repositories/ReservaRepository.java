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

    @Query("SELECT r FROM Reserva r " +
            "WHERE LOWER(r.apellidoHuesped) LIKE LOWER(:apellido) " +
            "AND (:nombre IS NULL OR LOWER(r.nombreHuesped) LIKE LOWER(:nombre))")
    List<Reserva> buscarPorHuesped(@Param("apellido") String apellido,
                                   @Param("nombre") String nombre);

    @Query("SELECT r FROM Reserva r WHERE " +
            "(r.fechaIngreso < :hasta AND r.fechaEgreso > :desde) AND " +
            "(r.estado IS NULL OR " +
            "(r.estado != com.isi.desa.Model.Enums.EstadoReserva.CANCELADA AND r.estado != com.isi.desa.Model.Enums.EstadoReserva.FINALIZADA))")
    List<Reserva> findReservasEnRango(@Param("desde") LocalDateTime desde,
                                      @Param("hasta") LocalDateTime hasta);

    @Query("SELECT r FROM Reserva r WHERE r.habitacion.idHabitacion = :idHabitacion " +
            "AND :fecha >= r.fechaIngreso AND :fecha < r.fechaEgreso")
    Optional<Reserva> findReservaActivaPorHabitacion(@Param("idHabitacion") String idHabitacion,
                                                     @Param("fecha") LocalDate fecha);

    Optional<Reserva> findTopByOrderByIdReservaDesc();

    // --- CONSULTA PARA CANCELAR ---
    @Query("SELECT r FROM Reserva r WHERE " +
            // 1. Apellido (Obligatorio)
            "LOWER(r.apellidoHuesped) LIKE LOWER(CONCAT('%', :apellido, '%')) " +
            "AND " +
            // 2. Nombre (Opcional - Maneja vacío y null)
            "(:nombre IS NULL OR :nombre = '' OR LOWER(r.nombreHuesped) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
            "AND " +
            // 3. Estado (Solo RESERVADA o viejas NULL)
            "(r.estado IS NULL OR r.estado = com.isi.desa.Model.Enums.EstadoReserva.RESERVADA)")
    List<Reserva> buscarParaCancelar(
            @Param("apellido") String apellido,
            @Param("nombre") String nombre
    );
}