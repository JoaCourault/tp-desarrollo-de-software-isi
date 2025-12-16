package com.isi.desa.Service.Implementations;

import com.isi.desa.Dao.Interfaces.IEstadiaDAO;
import com.isi.desa.Dao.Interfaces.IHabitacionDAO;
import com.isi.desa.Dao.Interfaces.IHuespedDAO;
import com.isi.desa.Dao.Interfaces.IReservaDAO;
import com.isi.desa.Dao.Repositories.EstadiaRepository;
import com.isi.desa.Dao.Repositories.ReservaRepository;
import com.isi.desa.Dto.Estadia.CheckInRequestDTO;
import com.isi.desa.Dto.Estadia.EstadiaDTO;
import com.isi.desa.Dto.Estadia.HabitacionCheckInDTO;
import com.isi.desa.Model.Entities.Estadia.Estadia;
import com.isi.desa.Model.Entities.Habitacion.Habitacion;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Model.Entities.Reserva.Reserva;
import com.isi.desa.Service.Interfaces.IEstadiaService;
import com.isi.desa.Service.Interfaces.Validators.IEstadiaValidator;
import com.isi.desa.Utils.Mappers.EstadiaMapper;
import com.isi.desa.Utils.Mappers.ReservaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class EstadiaService implements IEstadiaService {

    @Autowired private IEstadiaDAO estadiaDAO;
    @Autowired private IHabitacionDAO habitacionDAO;
    @Autowired private IReservaDAO reservaDAO;
    @Autowired private IHuespedDAO huespedDAO;
    @Autowired private ReservaRepository reservaRepository;
    @Autowired private EstadiaRepository estadiaRepository;
    @Autowired private IEstadiaValidator validator;

    @Override
    @Transactional
    public void realizarCheckIn(CheckInRequestDTO request) {
        // Validaciones...
        if (request == null || request.habitaciones == null || request.habitaciones.isEmpty()) throw new RuntimeException("Sin datos.");
        if (request.idHuespedTitular == null) throw new RuntimeException("Titular obligatorio.");

        EstadiaDTO estadiaDTO = new EstadiaDTO();
        estadiaDTO.idHuespedTitular = request.idHuespedTitular;

        HabitacionCheckInDTO primeraHab = request.habitaciones.get(0);
        estadiaDTO.checkIn = primeraHab.fechaDesde;
        estadiaDTO.checkOut = (primeraHab.fechaHasta != null) ? primeraHab.fechaHasta : primeraHab.fechaDesde.plusDays(1);

        long noches = ChronoUnit.DAYS.between(primeraHab.fechaDesde.toLocalDate(), estadiaDTO.checkOut.toLocalDate());
        estadiaDTO.cantNoches = (int) (noches <= 0 ? 1 : noches);

        Set<String> ocupantes = new HashSet<>();
        ocupantes.add(request.idHuespedTitular);
        request.habitaciones.forEach(h -> { if (h.acompanantesIds != null) ocupantes.addAll(h.acompanantesIds); });
        estadiaDTO.idsOcupantes = new ArrayList<>(ocupantes);
        estadiaDTO.valorTotalEstadia = BigDecimal.ZERO;

        if (validator.validateCreate(estadiaDTO) != null) throw validator.validateCreate(estadiaDTO);

        Estadia estadiaEntity = EstadiaMapper.dtoToEntity(estadiaDTO);
        estadiaEntity.setIdEstadia(String.format("EST-%03d", estadiaRepository.count() + 1));
        estadiaEntity.setHuespedTitular(huespedDAO.getById(request.idHuespedTitular));

        BigDecimal total = BigDecimal.ZERO;
        Reserva reservaEnc = null;
        List<Habitacion> habs = new ArrayList<>();

        for (HabitacionCheckInDTO habDTO : request.habitaciones) {
            Habitacion habitacion = habitacionDAO.obtener(habDTO.idHabitacion);
            if (habitacion == null) continue;

            if (reservaEnc == null) {
                reservaEnc = reservaRepository.findReservasEnRango(habDTO.idHabitacion, habDTO.fechaDesde, habDTO.fechaHasta)
                        .stream().filter(r -> "RESERVADA".equalsIgnoreCase(r.getEstado())).findFirst().orElse(null);
                if (reservaEnc != null) {
                    reservaEnc.setEstado("EFECTIVIZADA");
                    reservaDAO.update(ReservaMapper.entityToDTO(reservaEnc));
                }
            }

            BigDecimal costo = (habitacion.getPrecio() != null ? habitacion.getPrecio() : BigDecimal.ZERO)
                    .multiply(new BigDecimal(estadiaDTO.cantNoches));
            total = total.add(costo);

            // 🔥 FIX: NO cambiamos el estado de la habitación aquí.
            habs.add(habitacion);
        }

        estadiaEntity.setValorTotalEstadia(total);
        estadiaEntity.setReserva(reservaEnc);
        estadiaEntity.setListaHabitaciones(habs);

        List<Huesped> listaHuespedes = new ArrayList<>();
        ocupantes.forEach(id -> { Huesped h = huespedDAO.getById(id); if (h != null) listaHuespedes.add(h); });
        estadiaEntity.setListaHuespedes(listaHuespedes);

        estadiaDAO.save(estadiaEntity);
    }
}