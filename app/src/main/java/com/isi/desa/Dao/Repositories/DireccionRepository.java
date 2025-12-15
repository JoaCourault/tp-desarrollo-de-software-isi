package com.isi.desa.Dao.Repositories;

import com.isi.desa.Model.Entities.Direccion.Direccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DireccionRepository extends JpaRepository<Direccion, String> {

    // Se asume que en tu clase 'Huesped', el campo ID se llama 'idHuesped'.
    // Si se llama solo 'id', cambia "d.huesped.idHuesped" por "d.huesped.id"
    @Query("SELECT d FROM Direccion d WHERE d.huesped.idHuesped = :idHuesped")
    Optional<Direccion> findByIdHuesped(@Param("idHuesped") String idHuesped);

}