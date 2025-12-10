package com.isi.desa.Dao.Repositories;

import com.isi.desa.Model.Entities.Direccion.Direccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DireccionRepository extends JpaRepository<Direccion, String> {
    Optional<Direccion> findByHuesped_IdHuesped(String idHuesped);
}
