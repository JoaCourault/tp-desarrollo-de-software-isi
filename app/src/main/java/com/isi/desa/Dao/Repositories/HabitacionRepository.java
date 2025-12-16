package com.isi.desa.Dao.Repositories;

import com.isi.desa.Model.Entities.Habitacion.Habitacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HabitacionRepository extends JpaRepository<Habitacion, String> {
}
