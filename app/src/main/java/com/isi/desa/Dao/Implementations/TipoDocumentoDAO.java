package com.isi.desa.Dao.Implementations;

import com.isi.desa.Dao.Interfaces.ITipoDocumentoDAO;
import com.isi.desa.Dao.Repositories.TipoDocumentoRepository;
import com.isi.desa.Model.Entities.Tipodocumento.TipoDocumento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoDocumentoDAO implements ITipoDocumentoDAO {
    @Autowired
    private TipoDocumentoRepository repo;

    @Override
    public List<TipoDocumento> obtenerTodos() {
        return this.repo.findAll();
    }

    @Override
    public TipoDocumento obtener(String id) {
        return this.repo.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontro TipoDocumento con id: " + id));
    }
}
