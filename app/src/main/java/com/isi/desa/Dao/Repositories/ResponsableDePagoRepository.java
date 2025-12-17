package com.isi.desa.Dao.Repositories;

import com.isi.desa.Model.Entities.ResponsableDePago.PersonaFisica;
import com.isi.desa.Model.Entities.ResponsableDePago.ResponsableDePago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResponsableDePagoRepository extends JpaRepository<ResponsableDePago, String> {
    @Query("SELECT pf FROM PersonaFisica pf WHERE pf.huesped.idHuesped = :idHuesped")
    PersonaFisica findPersonaFisicaByIdHuesped(@Param("idHuesped") String idHuesped);

    @Query("SELECT pj FROM PersonaJuridica pj WHERE pj.cuit = :cuit")
    List<ResponsableDePago> findPersonaJuridicaByCuit(@Param("cuit") String cuit);

    @Query("SELECT pf FROM PersonaFisica pf WHERE pf.huesped.cuit = :cuit")
    List<ResponsableDePago> findPersonaFisicaByCuit(@Param("cuit") String cuit);

    @Query("SELECT pj FROM PersonaJuridica pj WHERE LOWER(pj.razonSocial) LIKE LOWER(CONCAT('%', :razonSocial, '%'))")
    List<ResponsableDePago> findByRazonSocialContainingIgnoreCase(@Param("razonSocial") String razonSocial);

    // Devuelve todas las razones sociales distintas de PersonaJuridica
    @Query("SELECT DISTINCT(pj.razonSocial) FROM PersonaJuridica pj")
    List<String> findAllRazonesSociales();

    @Query("SELECT pj FROM PersonaJuridica pj WHERE pj.direccion.idDireccion = :idDireccion")
    List<ResponsableDePago> findPersonaJuridicaByDireccion(@Param("idDireccion") String idDireccion);
}
