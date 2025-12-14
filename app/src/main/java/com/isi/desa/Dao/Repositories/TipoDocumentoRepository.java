package com.isi.desa.Dao.Repositories;

import com.isi.desa.Model.Entities.Tipodocumento.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoDocumentoRepository extends JpaRepository<TipoDocumento, String> {

}
