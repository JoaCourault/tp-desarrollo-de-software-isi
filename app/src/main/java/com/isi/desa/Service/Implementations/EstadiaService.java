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
import com.isi.desa.Model.Entities.Habitacion.HabitacionEntity;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Model.Entities.Reserva.Reserva;
import com.isi.desa.Model.Enums.EstadoHabitacion;
import com.isi.desa.Service.Interfaces.IEstadiaService;
import com.isi.desa.Utils.Mappers.ReservaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class EstadiaService implements IEstadiaService {

    @Autowired private IEstadiaDAO estadiaDAO;
    @Autowired private IHabitacionDAO habitacionDAO;
    @Autowired private IHuespedDAO huespedDAO;
    @Autowired private IReservaDAO reservaDAO;
    @Autowired private ReservaRepository reservaRepository;
    @Autowired private EstadiaRepository estadiaRepository;

    @Override
    @Transactional
    public void realizarCheckIn(CheckInRequestDTO request) {

        // 1. Obtener titular
        Huesped titular = huespedDAO.getById(request.huespedTitular.idHuesped);
        if (titular == null) {
            throw new RuntimeException("Huésped titular no encontrado: " + request.huespedTitular.idHuesped);
        }

        // Acumuladores
        Float valorTotalAcumulado = 0.0f;
        List<String> habitacionesIdsParaEstadia = new ArrayList<>();
        String idReservaEncontrada = null;

        // Referencia de fechas (tomadas de la primera habitación)
        if (request.habitaciones == null || request.habitaciones.isEmpty()) {
            throw new RuntimeException("No se enviaron habitaciones para el check-in");
        }
        HabitacionCheckInDTO primeraHab = request.habitaciones.get(0);

        // 2. Bucle de habitaciones
        for (HabitacionCheckInDTO habDTO : request.habitaciones) {

            HabitacionEntity habitacion = habitacionDAO.obtener(habDTO.idHabitacion);
            if (habitacion == null) {
                throw new RuntimeException("Habitación no encontrada: " + habDTO.idHabitacion);
            }

            // IDs para la ManyToMany en Estadia
            habitacionesIdsParaEstadia.add(habitacion.getIdHabitacion());

            // Buscar (solo una vez) la reserva asociada en ese rango para esa habitación
            if (idReservaEncontrada == null) {
                List<Reserva> reservas = reservaRepository.findReservasEnRango(
                        habDTO.idHabitacion,
                        habDTO.fechaDesde.toLocalDate(),
                        habDTO.fechaHasta.toLocalDate()
                );

                Reserva r = reservas.stream()
                        .filter(res -> "RESERVADA".equalsIgnoreCase(res.getEstado()))
                        .findFirst()
                        .orElse(null);

                if (r != null) {
                    r.setEstado("EFECTIVIZADA");
                    reservaDAO.update(ReservaMapper.entityToDTO(r) );
                    idReservaEncontrada = r.getIdReserva();
                }
            }

            // Cálculo de noches
            long noches = ChronoUnit.DAYS.between(habDTO.fechaDesde, habDTO.fechaHasta);
            if (noches <= 0) noches = 1;

            // Precio por noche de la habitación
            Float precioNoche = habitacion.getPrecio() != null ? habitacion.getPrecio() : 0.0f;

            // Subtotal de esta habitación
            Float subtotal = precioNoche * noches;
            valorTotalAcumulado += subtotal;

            // Marcar habitación como OCUPADA
            habitacion.setEstado(EstadoHabitacion.OCUPADA);
            habitacionDAO.modificar(habitacion);
        }

        // 3. Crear EstadiaDTO
        EstadiaDTO estadiaDTO = new EstadiaDTO();

        long count = estadiaRepository.count();
        estadiaDTO.idEstadia = String.format("EST-%03d", count + 1);

        estadiaDTO.checkIn = primeraHab.fechaDesde;
        estadiaDTO.checkOut = primeraHab.fechaHasta;

        long nochesGrales = ChronoUnit.DAYS.between(primeraHab.fechaDesde, primeraHab.fechaHasta);
        estadiaDTO.cantNoches = (int) (nochesGrales <= 0 ? 1 : nochesGrales);

        estadiaDTO.valorTotalEstadia = valorTotalAcumulado;
        estadiaDTO.idReserva = idReservaEncontrada;

        // Titular
        estadiaDTO.idHuespedTitular = titular.getIdHuesped();

        // Habitaciones de la estadía
        estadiaDTO.idsHabitaciones = habitacionesIdsParaEstadia;

        // Ocupantes = titular + acompañantes
        List<String> ocupantesIds = new ArrayList<>();
        ocupantesIds.add(titular.getIdHuesped());

        if (request.acompanantesIds != null) {
            for (String id : request.acompanantesIds) {
                if (id != null && !id.equals(titular.getIdHuesped())) {
                    ocupantesIds.add(id);
                }
            }
        }

        estadiaDTO.idsOcupantes = ocupantesIds;

        // 4. Persistir la estadía completa
        estadiaDAO.crear(estadiaDTO);
    }
}
