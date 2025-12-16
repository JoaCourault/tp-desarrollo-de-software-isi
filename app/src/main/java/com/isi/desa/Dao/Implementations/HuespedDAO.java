package com.isi.desa.Dao.Implementations;

import com.isi.desa.Dao.Interfaces.IHuespedDAO;
import com.isi.desa.Dao.Repositories.HuespedRepository;
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

    @Override
    @Transactional
    public Huesped save(Huesped huesped) {
        return repository.save(huesped);
    }

    @Override
    @Transactional
    public Huesped modificar(HuespedDTO dto) {
        Huesped existente = repository.findById(dto.idHuesped)
                .orElseThrow(() -> new HuespedNotFoundException("No se encontró huésped con ID: " + dto.idHuesped));

        // Convertimos el DTO a Entidad.
        // IMPORTANTE: El Mapper buscará el TipoDocumento en BBDD via DAO estático.
        Huesped actualizado = HuespedMapper.dtoToEntity(dto);

        // Aseguramos que no se pierda el estado de eliminado original ni el ID
        actualizado.setIdHuesped(existente.getIdHuesped());
        actualizado.setEliminado(existente.isEliminado());

        // Si la dirección venía en el DTO, el mapper la creó.
        // Si no venía, mantenemos la anterior (opcional, depende de tu lógica de negocio).
        if(actualizado.getDireccion() == null && existente.getDireccion() != null){
            actualizado.setDireccion(existente.getDireccion());
        }

        return repository.save(actualizado);
    }

    @Override
    @Transactional
    public Huesped eliminar(String idHuesped) {
        Huesped existente = repository.findById(idHuesped)
                .orElseThrow(() -> new HuespedNotFoundException("No se encontró huesped con ID: " + idHuesped));

        existente.setEliminado(true);
        return repository.save(existente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Huesped> leerHuespedes() {
        return repository.findByEliminadoFalse();
    }

    @Override @Transactional(readOnly = true)
    public Huesped obtenerHuesped(String DNI) {
        // Implementar si tienes un método findByNumDoc en el repo
        return null;
    }

    @Override @Transactional(readOnly = true)
    public Huesped getById(String id) {
        return repository.findById(id).orElse(null);
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

    @Override
    @Transactional
    public void agregarEstadiaAHuesped(String idHuesped, String idEstadia) {
        String sql = "INSERT INTO huesped_estadia (id_huesped, id_estadia) VALUES (:idHuesped, :idEstadia)";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("idHuesped", idHuesped);
        query.setParameter("idEstadia", idEstadia);
        query.executeUpdate();
    }
}