package com.isi.desa.Dao.Repositories;

import com.isi.desa.Model.Entities.Huesped.Huesped;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HuespedRepository extends JpaRepository<Huesped, String> {

    @Query(value = "SELECT * FROM huesped h WHERE " +
            "(h.eliminado = false) AND " +
            "(CAST(:nombre AS text) IS NULL OR h.nombre ILIKE :nombre) AND " +
            "(CAST(:apellido AS text) IS NULL OR h.apellido ILIKE :apellido) AND " +
            "(CAST(:numDoc AS text) IS NULL OR h.num_doc LIKE :numDoc) AND " +
            "(CAST(:tipoDoc AS text) IS NULL OR h.tipo_doc = :tipoDoc)",
            nativeQuery = true)
    List<Huesped> buscarConFiltros(
            @Param("nombre") String nombre,
            @Param("apellido") String apellido,
            @Param("numDoc") String numDoc,
            @Param("tipoDoc") String tipoDoc
    );

    boolean existsByNumDocAndTipoDoc_TipoDocumento(String numDoc, String tipoDocumento);

    boolean existsByNumDocAndTipoDoc_TipoDocumentoAndIdHuespedNot(String numDoc, String tipoDocumento, String idHuesped);

}