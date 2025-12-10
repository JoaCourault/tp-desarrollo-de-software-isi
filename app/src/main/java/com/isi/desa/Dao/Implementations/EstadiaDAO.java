package com.isi.desa.Dao.Implementations;

import com.isi.desa.Dao.Interfaces.IEstadiaDAO;
import com.isi.desa.Dao.Repositories.EstadiaRepository;
import com.isi.desa.Dao.Repositories.HabitacionRepository;
import com.isi.desa.Dao.Repositories.HuespedRepository;
import com.isi.desa.Dao.Repositories.ReservaRepository;
import com.isi.desa.Dto.Estadia.EstadiaDTO;
import com.isi.desa.Model.Entities.Estadia.Estadia;
import com.isi.desa.Model.Entities.Habitacion.HabitacionEntity;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Model.Entities.Reserva.Reserva;
import com.isi.desa.Utils.Mappers.EstadiaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service("estadiaDAO")
public class EstadiaDAO implements IEstadiaDAO {

    @Autowired
    private EstadiaRepository repository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private HuespedRepository huespedRepository;

    @Autowired
    private HabitacionRepository habitacionRepository;

    @Override
    @Transactional
    public Estadia crear(EstadiaDTO dto) {

        // Generar ID si no viene uno
        if (dto.idEstadia == null || dto.idEstadia.isBlank()) {
            long count = repository.count();
            dto.idEstadia = String.format("EST-%03d", count + 1);
        }

        // Mapeo base (campos simples)
        Estadia nuevaEstadia = EstadiaMapper.dtoToEntity(dto);

        // 1. Reserva asociada (0..1)
        if (dto.idReserva != null) {
            Reserva reserva = reservaRepository.findById(dto.idReserva)
                    .orElseThrow(() -> new RuntimeException("Reserva no encontrada: " + dto.idReserva));
            nuevaEstadia.setReserva(reserva);
        }

        // 2. Huesped titular (ManyToOne -> columna id_huesped_titular en ESTADIA)
        if (dto.idHuespedTitular != null) {
            Huesped titular = huespedRepository.findById(dto.idHuespedTitular)
                    .orElseThrow(() -> new RuntimeException("Titular no encontrado: " + dto.idHuespedTitular));
            nuevaEstadia.setHuespedTitular(titular);
        }

        // 3. Lista de huéspedes (ManyToMany -> tabla huesped_estadia)
        if (dto.idsOcupantes != null && !dto.idsOcupantes.isEmpty()) {
            List<Huesped> ocupantes = huespedRepository.findAllById(dto.idsOcupantes);
            nuevaEstadia.setListaHuespedes(ocupantes);
        }

        // 4. Lista de habitaciones (ManyToMany -> tabla habitacion_estadia)
        if (dto.idsHabitaciones != null && !dto.idsHabitaciones.isEmpty()) {
            List<HabitacionEntity> habitaciones = habitacionRepository.findAllById(dto.idsHabitaciones);
            nuevaEstadia.setListaHabitaciones(habitaciones);
        }

        return repository.save(nuevaEstadia);
    }

    // Métodos estándar

    @Override
    @Transactional
    public EstadiaDTO save(Estadia estadia) {
        Estadia saved = repository.save(estadia);
        return EstadiaMapper.entityToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Estadia> findById(String id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Estadia> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public Estadia modificar(EstadiaDTO dto) {
        Estadia entity = EstadiaMapper.dtoToEntity(dto);

        // Opcional: repetir lógica de relaciones como en crear(...)
        // si necesitás que modificar también actualice reserva, titular, ocupantes, etc.

        return repository.save(entity);
    }

    @Override
    @Transactional
    public Estadia eliminar(String id) {
        Optional<Estadia> entityOpt = repository.findById(id);
        entityOpt.ifPresent(repository::delete);
        return entityOpt.orElse(null);
    }
}
