package com.isi.desa.Dao.Implementations;

import com.isi.desa.Dao.Interfaces.IHuespedDAO;
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

@Service("huespedDAO")
public class HuespedDAO implements IHuespedDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private HuespedRepository repository;

    @Autowired
    private HuespedMapper mapper;

    // ============================================================
    // CREAR HUESPED
    // ============================================================
    @Override
    @Transactional
    public Huesped crear(HuespedDTO dto) throws HuespedDuplicadoException {

        // Si ya existe ID → error
        if (dto.idHuesped != null && repository.existsById(dto.idHuesped)) {
            throw new HuespedDuplicadoException("Ya existe un huesped con el ID: " + dto.idHuesped);
        }

        // Generar ID automático HU-###
        if (dto.idHuesped == null || dto.idHuesped.isBlank()) {
            long count = repository.count();
            dto.idHuesped = String.format("HU-%03d", count + 1);
        }

        // Mapear DTO a entidad
        Huesped nuevo = mapper.dtoToEntity(dto);
        nuevo.setEliminado(false);

        return repository.save(nuevo);
    }

    // ============================================================
    // MODIFICAR HUESPED
    // ============================================================
    @Override
    @Transactional
    public Huesped modificar(HuespedDTO dto) {

        Huesped existente = repository.findById(dto.idHuesped)
                .orElseThrow(() -> new HuespedNotFoundException("No se encontró huésped con ID: " + dto.idHuesped));

        Huesped actualizado = mapper.dtoToEntity(dto);

        // Mantener valor de eliminado
        actualizado.setEliminado(existente.isEliminado());

        return repository.save(actualizado);
    }

    // ============================================================
    // ELIMINAR (SOFT DELETE)
    // ============================================================
    @Override
    @Transactional
    public Huesped eliminar(String idHuesped) {
        Huesped existente = repository.findById(idHuesped)
                .orElseThrow(() -> new HuespedNotFoundException("No se encontró huesped con ID: " + idHuesped));

        // Validar que no tenga estadías
        if (!obtenerEstadiasDeHuesped(idHuesped).isEmpty()) {
            throw new HuespedConEstadiaAsociadasException(
                    "El huésped tiene estadías asociadas y no puede eliminarse."
            );
        }

        existente.setEliminado(true);
        return repository.save(existente);
    }

    // ============================================================
    // OBTENER POR DNI
    // ============================================================
    @Override
    @Transactional(readOnly = true)
    public Huesped obtenerHuesped(String DNI) {
        return repository.findAll().stream()
                .filter(h -> !h.isEliminado())
                .filter(h -> h.getNumDoc() != null && h.getNumDoc().equalsIgnoreCase(DNI))
                .findFirst()
                .orElseThrow(() -> new HuespedNotFoundException("No se encontró huesped con DNI: " + DNI));
    }

    // ============================================================
    // LISTAR
    // ============================================================
    @Override
    @Transactional(readOnly = true)
    public List<Huesped> leerHuespedes() {
        return repository.findAll().stream()
                .filter(h -> !h.isEliminado())
                .toList();
    }

    // ============================================================
    // OBTENER POR ID
    // ============================================================
    @Override
    @Transactional(readOnly = true)
    public Huesped getById(String id) {
        return repository.findById(id)
                .filter(h -> !h.isEliminado())
                .orElseThrow(() -> new HuespedNotFoundException("No se encontró huesped con ID: " + id));
    }

    // ============================================================
    // ASOCIAR ESTADÍA
    // ============================================================
    @Override
    @Transactional
    public void agregarEstadiaAHuesped(String idHuesped, String idEstadia) {

        String sql = """
                INSERT INTO huesped_estadia(id_huesped, id_estadia)
                VALUES(:idHuesped, :idEstadia)
                """;

        Query q = entityManager.createNativeQuery(sql);
        q.setParameter("idHuesped", idHuesped);
        q.setParameter("idEstadia", idEstadia);
        q.executeUpdate();
    }

    // ============================================================
    // OBTENER ESTADÍAS
    // ============================================================
    @Override
    @Transactional(readOnly = true)
    public List<Estadia> obtenerEstadiasDeHuesped(String idHuesped) {

        String sql = """
            SELECT e.* 
            FROM estadia e
            INNER JOIN huesped_estadia he ON e.id_estadia = he.id_estadia
            WHERE he.id_huesped = :idHuesped
        """;

        Query q = entityManager.createNativeQuery(sql, Estadia.class);
        q.setParameter("idHuesped", idHuesped);

        return q.getResultList();
    }
}
