package com.isi.desa.Service.Implementations;

import com.isi.desa.Dao.Interfaces.IEstadiaDAO;
import com.isi.desa.Dao.Interfaces.IHabitacionDAO;
import com.isi.desa.Dao.Interfaces.IHuespedDAO;
import com.isi.desa.Dao.Repositories.ReservaRepository;
import com.isi.desa.Dto.Estadia.CheckInRequestDTO;
import com.isi.desa.Dto.Estadia.HabitacionCheckInDTO;
import com.isi.desa.Model.Entities.Estadia.Estadia;
import com.isi.desa.Model.Entities.Habitacion.HabitacionEntity;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Model.Entities.Reserva.Reserva;
import com.isi.desa.Model.Enums.EstadoHabitacion;
import com.isi.desa.Service.Interfaces.IEstadiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class EstadiaService implements IEstadiaService {

    @Autowired private IEstadiaDAO estadiaDAO;
    @Autowired private IHabitacionDAO habitacionDAO;
    @Autowired private IHuespedDAO huespedDAO;
    @Autowired private ReservaRepository reservaRepository;

    @Override
    @Transactional
    public void realizarCheckIn(CheckInRequestDTO request) {
        Huesped titular = huespedDAO.getById(request.huespedTitular.idHuesped);

        for (HabitacionCheckInDTO habDTO : request.habitaciones) {

            HabitacionEntity habitacion = habitacionDAO.obtener(habDTO.idHabitacion);
            if (habitacion == null) throw new RuntimeException("Habitación no encontrada");


            // 2. Crear Estadía
            Estadia estadia = new Estadia();
            estadia.setIdEstadia("EST-" + UUID.randomUUID().toString().substring(0, 8));
            estadia.setCheckIn(habDTO.fechaDesde);
            estadia.setCheckOut(habDTO.fechaHasta);
            estadia.setIdHabitacion(habitacion.getIdHabitacion());

            long noches = ChronoUnit.DAYS.between(habDTO.fechaDesde, habDTO.fechaHasta);
            if (noches == 0) noches = 1;
            estadia.setCantNoches((int) noches);

            if (habitacion.getPrecio() != null) {
                estadia.setValorTotalEstadia(habitacion.getPrecio().multiply(new java.math.BigDecimal(noches)));
            } else {
                estadia.setValorTotalEstadia(java.math.BigDecimal.ZERO);
            }

            //  LÓGICA AUTOMÁTICA DE RESERVA
            // Buscamos si existe una reserva "RESERVADA" para esta habitación y fechas
            List<Reserva> reservasEncontradas = reservaRepository.findReservasEnRango(
                    habDTO.idHabitacion,
                    habDTO.fechaDesde.toLocalDate(), // Asumiendo que tu repo usa LocalDate
                    habDTO.fechaHasta.toLocalDate()
            );

            // Filtramos solo las que estén en estado "RESERVADA"
            Reserva reservaEfectiva = reservasEncontradas.stream()
                    .filter(r -> "RESERVADA".equalsIgnoreCase(r.getEstado()))
                    .findFirst()
                    .orElse(null);

            if (reservaEfectiva != null) {
                // CASO: CHECK-IN DE RESERVA
                estadia.setIdReserva(reservaEfectiva.getIdReserva());

                // Efectivizamos la reserva para que ya no figure pendiente
                reservaEfectiva.setEstado("EFECTIVIZADA"); // o "COMPLETADA"
                reservaRepository.save(reservaEfectiva);
            } else {
                // CASO: WALK-IN (Sin reserva previa)
                estadia.setIdReserva(null);
            }

            estadia.setIdFactura(null);
            estadiaDAO.save(estadia);

            // 3. Vincular Huéspedes
            huespedDAO.agregarEstadiaAHuesped(titular.getIdHuesped(), estadia.getIdEstadia());
            if (request.acompanantesIds != null) {
                for (String idAcompanante : request.acompanantesIds) {
                    huespedDAO.agregarEstadiaAHuesped(idAcompanante, estadia.getIdEstadia());
                }
            }
        }
    }
}