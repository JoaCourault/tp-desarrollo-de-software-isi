package com.isi.desa.Dao.Implementations;

import com.isi.desa.Dao.Interfaces.IHuespedDAO;
import com.isi.desa.Dao.Repositories.EstadiaRepository;
import com.isi.desa.Dao.Repositories.HuespedRepository;
import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Exceptions.Huesped.HuespedConEstadiaAsociadasException;
import com.isi.desa.Exceptions.Huesped.HuespedDuplicadoException;
import com.isi.desa.Exceptions.Huesped.HuespedNotFoundException;
import com.isi.desa.Model.Entities.Estadia.Estadia;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Utils.Mappers.HuespedMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HuespedDAO implements IHuespedDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private HuespedRepository repository;

    @Autowired
    private EstadiaRepository estadiaRepository;

    @Autowired
    private HuespedMapper huespedMapper; // <--- 1. INYECCIÓN DEL MAPPER

    @Override
    @Transactional
    public Huesped crear(HuespedDTO huesped) throws HuespedDuplicadoException {
        if (huesped.idHuesped != null && repository.existsById(huesped.idHuesped)) {
            throw new HuespedDuplicadoException("Ya existe un huesped con el ID: " + huesped.idHuesped);
        }
        // Generar ID incremental si no viene (HU-###)
        if (huesped.idHuesped == null || huesped.idHuesped.isBlank()) {
            long count = repository.count();
            huesped.idHuesped = String.format("HU-%03d", count + 1);
        }

        // 2. USO DE INSTANCIA (NO STATIC)
        Huesped nuevo = huespedMapper.dtoToEntity(huesped);
        nuevo.setEliminado(false);
        return repository.save(nuevo);
    }

    @Override
    @Transactional
    public Huesped modificar(HuespedDTO dto) {
        Huesped existente = repository.findById(dto.idHuesped)
                .orElseThrow(() -> new HuespedNotFoundException("No se encontró huésped con ID: " + dto.idHuesped));

        // 2. USO DE INSTANCIA (NO STATIC)
        Huesped actualizado = huespedMapper.dtoToEntity(dto);

        // Preservar estado de eliminado del original
        actualizado.setEliminado(existente.isEliminado());

        return repository.save(actualizado);
    }

    @Override
    @Transactional
    public Huesped eliminar(String idHuesped) {
        Huesped existente = repository.findById(idHuesped)
                .orElseThrow(() -> new HuespedNotFoundException("No se encontro huesped con ID: " + idHuesped));

        if (!obtenerEstadiasDeHuesped(idHuesped).isEmpty()) {
            throw new HuespedConEstadiaAsociadasException("El huesped tiene estadias asociadas y no puede eliminarse.");
        }

        existente.setEliminado(true);
        return repository.save(existente);
    }

    @Override
    @Transactional(readOnly = true)
    public Huesped obtenerHuesped(String DNI) {
        return repository.findAll().stream()
                .filter(h -> !h.isEliminado())
                .filter(h -> h.getNumDoc() != null && h.getNumDoc().equals(DNI))
                .findFirst()
                .orElseThrow(() -> new HuespedNotFoundException("No se encontro huesped con DNI: " + DNI));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Huesped> leerHuespedes() {
        return repository.findAll().stream()
                .filter(h -> !h.isEliminado())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Huesped getById(String id) {
        return repository.findById(id)
                .filter(h -> !h.isEliminado())
                .orElseThrow(() -> new HuespedNotFoundException("No se encontro huesped con ID: " + id));
    }

    @Override
    @Transactional
    public void agregarEstadiaAHuesped(String idHuesped, String idEstadia) {
        String sql = "INSERT INTO huesped_estadia (id_huesped, id_estadia) VALUES (:idHuesped, :idEstadia)";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("idHuesped", idHuesped);
        query.setParameter("idEstadia", idEstadia);
        query.executeUpdate();
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<Estadia> obtenerEstadiasDeHuesped(String idHuesped) {
        String sql = "SELECT e.* FROM estadia e " +
                "INNER JOIN huesped_estadia he ON e.id_estadia = he.id_estadia " +
                "WHERE he.id_huesped = :idHuesped";
        Query query = entityManager.createNativeQuery(sql, Estadia.class);
        query.setParameter("idHuesped", idHuesped);
        return query.getResultList();
    }
}