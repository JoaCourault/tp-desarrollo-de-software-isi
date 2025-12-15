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
import com.isi.desa.Utils.Mappers.HabitacionMapper;
import com.isi.desa.Utils.Mappers.ReservaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        // 1. Validar Habitaciones
        if (request.habitaciones == null || request.habitaciones.isEmpty()) {
            throw new RuntimeException("No se enviaron habitaciones para el check-in");
        }

        // 2. Validar Titular (CORRECCIÓN: Usamos request.idHuespedTitular)
        Huesped titular = huespedDAO.getById(request.idHuespedTitular);
        if (titular == null) {
            throw new RuntimeException("Huésped titular no encontrado con ID: " + request.idHuespedTitular);
        }

        int edad = Period.between(titular.getFechaNac(), LocalDate.now()).getYears();

        if (edad < 18) {
            throw new RuntimeException("El titular debe ser mayor de 18 años. Edad actual: " + edad);
        }
        // Acumuladores
        Float valorTotalAcumulado = 0f;
        List<String> habitacionesIdsParaEstadia = new ArrayList<>();
        // Usamos un Set para evitar duplicados si la misma persona está en varias habitaciones (raro, pero posible)
        Set<String> todosLosOcupantesIds = new HashSet<>();

        // El titular siempre cuenta como ocupante (al menos administrativo)
        todosLosOcupantesIds.add(titular.getIdHuesped());

        String idReservaEncontrada = null;
        HabitacionCheckInDTO primeraHab = request.habitaciones.get(0);

        // --- BUCLE POR HABITACIÓN ---
        for (HabitacionCheckInDTO habDTO : request.habitaciones) {

            HabitacionEntity habitacion = habitacionDAO.obtener(habDTO.idHabitacion);
            if (habitacion == null) {
                throw new RuntimeException("Habitación no encontrada: " + habDTO.idHabitacion);
            }

            habitacionesIdsParaEstadia.add(habitacion.getIdHabitacion());

            // A. Recolectar Ocupantes de esta habitación
            if (habDTO.acompanantesIds != null) {
                for (String idOcupante : habDTO.acompanantesIds) {
                    if (idOcupante != null && !idOcupante.isEmpty()) {
                        todosLosOcupantesIds.add(idOcupante);
                    }
                }
            }

            // B. Buscar Reserva
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
                    reservaDAO.update(ReservaMapper.entityToDTO(r));
                    idReservaEncontrada = r.getIdReserva();
                }
            }

            // C. Costos
            long noches = ChronoUnit.DAYS.between(habDTO.fechaDesde, habDTO.fechaHasta);
            if (noches <= 0) noches = 1;

            Float precioNoche = habitacion.getPrecio() != null ? habitacion.getPrecio() : 0.0f;
            valorTotalAcumulado += (precioNoche * noches);

            // D. Actualizar Estado Habitación
            habitacion.setEstado(EstadoHabitacion.OCUPADA);
            habitacionDAO.modificar(HabitacionMapper.entityToDTO(habitacion));
        }

        // --- CREAR ESTADÍA ---
        EstadiaDTO estadiaDTO = new EstadiaDTO();
        long count = estadiaRepository.count();
        estadiaDTO.idEstadia = String.format("EST-%03d", count + 1);

        estadiaDTO.checkIn = primeraHab.fechaDesde;
        estadiaDTO.checkOut = primeraHab.fechaHasta;

        long nochesGrales = ChronoUnit.DAYS.between(primeraHab.fechaDesde, primeraHab.fechaHasta);
        estadiaDTO.cantNoches = (int) (nochesGrales <= 0 ? 1 : nochesGrales);

        estadiaDTO.valorTotalEstadia = valorTotalAcumulado;
        estadiaDTO.idReserva = idReservaEncontrada;
        estadiaDTO.idHuespedTitular = titular.getIdHuesped();
        estadiaDTO.idsHabitaciones = habitacionesIdsParaEstadia;

        // Convertimos el Set acumulado a Lista para el DTO
        estadiaDTO.idsOcupantes = new ArrayList<>(todosLosOcupantesIds);

        // --- PERSISTIR ---
        estadiaDAO.crear(estadiaDTO);
    }
}