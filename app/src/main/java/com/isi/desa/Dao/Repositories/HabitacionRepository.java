package com.isi.desa.Dao.Repositories;

import com.isi.desa.Dao.Repositories.Projections.HabitacionResumen;
import com.isi.desa.Model.Entities.Habitacion.Habitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HabitacionRepository extends JpaRepository<Habitacion, String> {

    @Query(value = "SELECT h.id_habitacion as idHabitacion, h.numero, h.precio, " +
            "h.detalles, h.capacidad, h.estado, h.tipo_habitacion as tipoHabitacionStr " +
            "FROM habitacion h", nativeQuery = true)
    List<HabitacionResumen> findAllResumen();
}