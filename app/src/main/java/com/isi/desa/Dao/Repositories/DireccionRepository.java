package com.isi.desa.Dao.Repositories;

import com.isi.desa.Model.Entities.Direccion.Direccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DireccionRepository extends JpaRepository<Direccion, String> {

}
