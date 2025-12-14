package com.isi.desa.Dao.Repositories;

import com.isi.desa.Model.Entities.Factura.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, String> {
}
