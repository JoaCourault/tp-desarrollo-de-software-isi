package com.isi.desa.Dao.Repositories;

import com.isi.desa.Model.Entities.Servicio.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, String> {
    List<Servicio> findByEstadia_IdEstadia(String idEstadia);
}
