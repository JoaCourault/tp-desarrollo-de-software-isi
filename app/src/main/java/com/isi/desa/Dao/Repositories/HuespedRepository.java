package com.isi.desa.Dao.Repositories;

import com.isi.desa.Model.Entities.Huesped.Huesped;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HuespedRepository extends JpaRepository<Huesped, String> {
    List<Huesped> findByEliminadoFalse();
}
