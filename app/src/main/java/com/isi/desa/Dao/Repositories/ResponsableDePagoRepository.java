package com.isi.desa.Dao.Repositories;

import com.isi.desa.Model.Entities.ResponsableDePago.PersonaFisica;
import com.isi.desa.Model.Entities.ResponsableDePago.ResponsableDePago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponsableDePagoRepository extends JpaRepository<ResponsableDePago, String> {
    @Query("SELECT pf FROM PersonaFisica pf WHERE pf.huesped.idHuesped = :idHuesped")
    PersonaFisica findPersonaFisicaByIdHuesped(@Param("idHuesped") String idHuesped);
}
