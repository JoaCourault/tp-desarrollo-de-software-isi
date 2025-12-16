package com.isi.desa.Dao.Implementations;

import com.isi.desa.Dao.Interfaces.IReservaDAO;
import com.isi.desa.Dao.Repositories.HabitacionRepository;
import com.isi.desa.Dao.Repositories.ReservaRepository;
import com.isi.desa.Dto.Reserva.ReservaDTO;
import com.isi.desa.Model.Entities.Reserva.Reserva;
import com.isi.desa.Utils.Mappers.ReservaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service("reservaDAO")
public class ReservaDAO implements IReservaDAO {

    @Autowired
    private ReservaRepository reservaRepo;

    @Autowired
    private HabitacionRepository habitacionRepo;

    @Override
    public Reserva getById(String id) {
        return reservaRepo.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Reserva update(ReservaDTO dto) {
        Reserva existente = reservaRepo.findById(dto.idReserva)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada: " + dto.idReserva));

        Reserva actualizado = ReservaMapper.updateEntityFromDTO(existente, dto);
        return reservaRepo.save(actualizado);
    }

    @Override
    public Reserva save(Reserva r) {
        return reservaRepo.save(r);
    }

    @Override
    public void deleteById(String id) {
        reservaRepo.deleteById(id);
    }


    @Override
    public List<Reserva> buscarReservasSolapadas(String idHabitacion, LocalDate desde, LocalDate hasta) {
        LocalDateTime desdeTs = (desde != null) ? desde.atTime(14, 0) : null;
        LocalDateTime hastaTs = (hasta != null) ? hasta.atTime(10, 0) : null;
        return reservaRepo.findReservasEnRango(idHabitacion, desdeTs, hastaTs);
    }

    @Override
    public List<ReservaDTO> findAllDTO() {
        return ReservaMapper.entityListToDTOList(reservaRepo.findAll());
    }

    @Override
    public ReservaDTO findByIdDTO(String id) {
        Reserva r = reservaRepo.findById(id).orElse(null);
        return ReservaMapper.entityToDTO(r);
    }
}
