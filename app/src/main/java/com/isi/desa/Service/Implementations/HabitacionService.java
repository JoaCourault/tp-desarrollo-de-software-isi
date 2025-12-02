package com.isi.desa.Service.Implementations;

import com.isi.desa.Dao.Interfaces.IHabitacionDAO;
import com.isi.desa.Dao.Repositories.EstadiaRepository;
import com.isi.desa.Dao.Repositories.ReservaRepository;
import com.isi.desa.Dto.Habitacion.DisponibilidadDiaDTO;
import com.isi.desa.Dto.Habitacion.HabitacionDTO;
import com.isi.desa.Dto.Habitacion.HabitacionDisponibilidadDTO;
import com.isi.desa.Model.Entities.Estadia.Estadia;
import com.isi.desa.Model.Entities.Habitacion.HabitacionEntity;
import com.isi.desa.Model.Entities.Reserva.Reserva;
import com.isi.desa.Model.Enums.EstadoHabitacion;
import com.isi.desa.Service.Interfaces.IHabitacionService;
import com.isi.desa.Utils.Mappers.HabitacionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class HabitacionService implements IHabitacionService {

    @Autowired
    private IHabitacionDAO dao;

    @Autowired
    private ReservaRepository reservaRepo;

    @Autowired
    private EstadiaRepository estadiaRepo;

    @Override
    public HabitacionDTO crear(HabitacionDTO dto) {
        HabitacionEntity entity = HabitacionMapper.dtoToEntity(dto);
        HabitacionEntity creado = dao.crear(entity);
        return HabitacionMapper.entityToDTO(creado);
    }

    @Override
    public HabitacionDTO modificar(HabitacionDTO dto) {
        HabitacionEntity entity = HabitacionMapper.dtoToEntity(dto);
        HabitacionEntity modificado = dao.modificar(entity);
        return HabitacionMapper.entityToDTO(modificado);
    }

    @Override
    public List<HabitacionDTO> listar() {
        return dao.listar().stream()
                .map(HabitacionMapper::entityToDTO)
                .toList();
    }

    @Override
    public List<HabitacionDisponibilidadDTO> obtenerDisponibilidad(LocalDate desde, LocalDate hasta) {
        List<HabitacionDTO> habitaciones = this.listar();
        List<HabitacionDisponibilidadDTO> resultado = new ArrayList<>();

        long dias = ChronoUnit.DAYS.between(desde, hasta) + 1;

        LocalDateTime desdeTime = desde.atStartOfDay();
        LocalDateTime hastaTime = hasta.atTime(LocalTime.MAX);

        for (HabitacionDTO h : habitaciones) {
            HabitacionDisponibilidadDTO fila = new HabitacionDisponibilidadDTO();
            fila.habitacion = h;
            fila.disponibilidad = new ArrayList<>();

            List<Reserva> reservasEnRango = reservaRepo.findReservasEnRango(h.id_habitacion, desde, hasta);

            List<Estadia> estadiasEnRango = estadiaRepo.findEstadiasEnRango(h.id_habitacion, desdeTime, hastaTime);

            for (int i = 0; i < dias; i++) {
                DisponibilidadDiaDTO dia = new DisponibilidadDiaDTO();
                LocalDate fechaActual = desde.plusDays(i);
                dia.fecha = fechaActual;

                // --- LÓGICA DE PRIORIDAD DE ESTADOS ---

                // PRIORIDAD 1: Bloqueo Global (Mantenimiento)
                if (h.estado != null &&
                        (h.estado.name().equals("MANTENIMIENTO") || h.estado.name().equals("FUERA_DE_SERVICIO"))) {
                    dia.estado = h.estado.name();
                }
                else {
                    // PRIORIDAD 2: Estadía (Check-In activo) -> "OCUPADA"
                    boolean hayEstadia = estadiasEnRango.stream().anyMatch(estadia -> {
                        LocalDate checkIn = estadia.getCheckIn().toLocalDate();
                        LocalDate checkOut = estadia.getCheckOut().toLocalDate();
                        // el dia cuenta si es >= checkIn y < checkOut
                        // O <= checkOut si queremos marcar el dia de salida tambien.
                        return !fechaActual.isBefore(checkIn) && !fechaActual.isAfter(checkOut);
                    });

                    if (hayEstadia) {
                        dia.estado = "OCUPADA";
                    }
                    else {
                        // PRIORIDAD 3: Reserva -> "RESERVADA"
                        boolean hayReserva = reservasEnRango.stream().anyMatch(reserva ->
                                (fechaActual.isEqual(reserva.getFechaDesde()) || fechaActual.isAfter(reserva.getFechaDesde())) &&
                                        (fechaActual.isEqual(reserva.getFechaHasta()) || fechaActual.isBefore(reserva.getFechaHasta())) &&
                                        "RESERVADA".equalsIgnoreCase(reserva.getEstado()) // Solo mostramos si sigue reservada, no cancelada o efectivizada
                        );

                        if (hayReserva) {
                            dia.estado = "RESERVADA";
                        } else {
                            // PRIORIDAD 4: Libre
                            dia.estado = "DISPONIBLE";
                        }
                    }
                }
                fila.disponibilidad.add(dia);
            }
            resultado.add(fila);
        }
        return resultado;
    }
}