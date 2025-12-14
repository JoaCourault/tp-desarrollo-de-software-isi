package com.isi.desa.Dao.Repositories;

import com.isi.desa.Model.Entities.Huesped.Huesped;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HuespedRepository extends JpaRepository<Huesped, String> {
}
