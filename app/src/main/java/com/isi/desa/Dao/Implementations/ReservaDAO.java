package com.isi.desa.Dao.Implementations;

import com.isi.desa.Dao.Interfaces.IReservaDAO;
import com.isi.desa.Dao.Repositories.HabitacionRepository;
import com.isi.desa.Dao.Repositories.ReservaRepository;
import com.isi.desa.Dto.Reserva.ReservaDTO;
import com.isi.desa.Model.Entities.Habitacion.HabitacionEntity;
import com.isi.desa.Model.Entities.Reserva.Reserva;
import com.isi.desa.Utils.Mappers.ReservaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service("reservaDAO")
public class ReservaDAO implements IReservaDAO {

    @Autowired
    private ReservaRepository reservaRepo;

    @Autowired
    private HabitacionRepository habitacionRepo;

    // ===============================================================
    // GET BY ID
    // ===============================================================
    @Override
    public Reserva getById(String id) {
        return reservaRepo.findById(id).orElse(null);
    }

    // ===============================================================
    // CREAR RESERVA A PARTIR DE DTO
    // ===============================================================
    @Override
    @Transactional
    public Reserva crear(ReservaDTO dto) {

        Reserva reserva = ReservaMapper.dtoToEntity(dto);

        // generar ID si no vino
        if (reserva.getIdReserva() == null || reserva.getIdReserva().isBlank()) {
            long count = reservaRepo.count();
            reserva.setIdReserva(String.format("RES-%03d", count + 1));
        }

        // relacion habitacion
        HabitacionEntity hab = habitacionRepo.findById(dto.idHabitacion)
                .orElseThrow(() -> new RuntimeException("Habitación no encontrada: " + dto.idHabitacion));

        reserva.setHabitacion(hab);

        return reservaRepo.save(reserva);
    }

    // ===============================================================
    // UPDATE
    // ===============================================================
    @Override
    @Transactional
    public Reserva update(ReservaDTO dto) {

        Reserva existente = reservaRepo.findById(dto.idReserva)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada: " + dto.idReserva));

        Reserva actualizado = ReservaMapper.updateEntityFromDTO(existente, dto);
        return reservaRepo.save(actualizado);
    }

    // ===============================================================
    // SAVE (entity directa)
    // ===============================================================
    @Override
    public Reserva save(Reserva r) {
        return reservaRepo.save(r);
    }

    // ===============================================================
    // DELETE
    // ===============================================================
    @Override
    public void deleteById(String id) {
        reservaRepo.deleteById(id);
    }

    // ===============================================================
    // BUSCAR RESERVAS SOLAPADAS
    // ===============================================================
    @Override
    public List<Reserva> buscarReservasSolapadas(String idHabitacion, LocalDate desde, LocalDate hasta) {
        return reservaRepo.findReservasEnRango(idHabitacion, desde, hasta);
    }

    // ===============================================================
    // FIND ALL (DTO)
    // ===============================================================
    @Override
    public List<ReservaDTO> findAllDTO() {
        return ReservaMapper.entityListToDTOList(reservaRepo.findAll());
    }

    // ===============================================================
    // FIND BY ID (DTO)
    // ===============================================================
    @Override
    public ReservaDTO findByIdDTO(String id) {
        Reserva r = reservaRepo.findById(id).orElse(null);
        return ReservaMapper.entityToDTO(r);
    }
}
