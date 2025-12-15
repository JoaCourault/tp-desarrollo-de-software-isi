package com.isi.desa.Dao.Implementations;

import com.isi.desa.Dao.Interfaces.IHuespedDAO;
import com.isi.desa.Dao.Repositories.HuespedRepository;
import com.isi.desa.Dao.Repositories.DireccionRepository;
import com.isi.desa.Dto.Huesped.HuespedDTO;
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
    private DireccionRepository direccionRepository;

    @Override
    @Transactional
    public Huesped crear(HuespedDTO huespedDTO) {

        // 1. Generar ID Huesped si es nulo (Formato HU-001)
        if (huespedDTO.idHuesped == null || huespedDTO.idHuesped.isBlank()) {
            long count = repository.count();
            huespedDTO.idHuesped = String.format("HU-%03d", count + 1);
        }

        // 2. Generar ID Direccion si es nulo (Formato DIR-001)
        if (huespedDTO.direccion != null && (huespedDTO.direccion.id == null || huespedDTO.direccion.id.isBlank())) {
            long countDir = direccionRepository.count();
            huespedDTO.direccion.id = String.format("DIR-%03d", countDir + 1);
        }

        // 3. Convertir y Guardar
        Huesped nuevo = HuespedMapper.dtoToEntity(huespedDTO);
        nuevo.setEliminado(false);

        // Al tener CascadeType.ALL, guardar huesped guarda la dirección
        return repository.save(nuevo);
    }

    @Override
    @Transactional
    public Huesped modificar(HuespedDTO dto) {
        Huesped existente = repository.findById(dto.idHuesped)
                .orElseThrow(() -> new HuespedNotFoundException("No se encontró huésped con ID: " + dto.idHuesped));

        Huesped actualizado = HuespedMapper.dtoToEntity(dto);
        actualizado.setEliminado(existente.isEliminado());
        return repository.save(actualizado);
    }

    @Override
    @Transactional
    public Huesped eliminar(String idHuesped) {
        Huesped existente = repository.findById(idHuesped)
                .orElseThrow(() -> new HuespedNotFoundException("No se encontro huesped con ID: " + idHuesped));

        // Lógica de validación de estadias (si es necesario)...

        existente.setEliminado(true);
        return repository.save(existente);
    }

    // --- Métodos de lectura ---
    @Override
    @Transactional(readOnly = true)
    public List<Huesped> leerHuespedes() {
        // Usamos el método que filtra por eliminado=false
        return repository.findByEliminadoFalse();
    }
    @Override @Transactional(readOnly = true) public Huesped obtenerHuesped(String DNI) { return null; }
    @Override @Transactional(readOnly = true) public Huesped getById(String id) { return repository.findById(id).orElse(null); }

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
    public List<Estadia> obtenerEstadiasDeHuesped(String idHuesped) {
        String sql = "SELECT e.* FROM estadia e " +
                "INNER JOIN huesped_estadia he ON e.id_estadia = he.id_estadia " +
                "WHERE he.id_huesped = :idHuesped";
        Query query = entityManager.createNativeQuery(sql, Estadia.class);
        query.setParameter("idHuesped", idHuesped);
        return query.getResultList();
    }
}