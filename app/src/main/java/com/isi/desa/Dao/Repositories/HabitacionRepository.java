package com.isi.desa.Dao.Repositories;

import com.isi.desa.Model.Entities.Habitacion.HabitacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HabitacionRepository extends JpaRepository<HabitacionEntity, String> {
}
