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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class HabitacionService implements IHabitacionService {

    @Autowired
    private IHabitacionDAO dao;

    @Autowired
    private ReservaRepository reservaRepo;

    @Autowired
    private EstadiaRepository estadiaRepo;

    @Override
    @Transactional
    public HabitacionDTO crear(HabitacionDTO dto) {
        // Delegamos al DAO que sabe instanciar la subclase correcta (Suite, Standard...)
        HabitacionEntity creado = dao.crear(dto);
        return HabitacionMapper.entityToDTO(creado);
    }

    @Override
    @Transactional
    public HabitacionDTO modificar(HabitacionDTO dto) {
        // Delegamos al DAO la modificación
        HabitacionEntity modificado = dao.modificar(dto);
        return HabitacionMapper.entityToDTO(modificado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HabitacionDTO> listar() {
        return dao.listar().stream()
                .map(HabitacionMapper::entityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HabitacionDisponibilidadDTO> obtenerDisponibilidad(LocalDate desde, LocalDate hasta) {
        // 1. Obtener todas las habitaciones
        List<HabitacionDTO> todas = this.listar();

        // 2. FILTRAR DUPLICADOS (Solución al error del Frontend)
        // Usamos un Map para asegurar que cada ID aparezca una sola vez.
        Map<String, HabitacionDTO> mapaUnico = todas.stream()
                .collect(Collectors.toMap(
                        h -> h.idHabitacion,  // Clave: ID de habitación
                        h -> h,               // Valor: El DTO
                        (existente, nuevo) -> existente // Si se repite, nos quedamos con el primero
                ));

        // Convertimos de nuevo a lista y ordenamos por número (opcional, para prolijidad)
        List<HabitacionDTO> habitacionesUnicas = new ArrayList<>(mapaUnico.values());
        habitacionesUnicas.sort((h1, h2) -> {
            if (h1.numero != null && h2.numero != null) return h1.numero.compareTo(h2.numero);
            return 0;
        });

        List<HabitacionDisponibilidadDTO> resultado = new ArrayList<>();

        long dias = ChronoUnit.DAYS.between(desde, hasta) + 1;
        LocalDateTime desdeTime = desde.atStartOfDay();
        LocalDateTime hastaTime = hasta.atTime(LocalTime.MAX);

        // 3. Iterar sobre la lista depurada
        for (HabitacionDTO h : habitacionesUnicas) {
            HabitacionDisponibilidadDTO fila = new HabitacionDisponibilidadDTO();
            fila.habitacion = h;
            fila.disponibilidad = new ArrayList<>();

            // Buscamos Reservas y Estadías asociadas
            List<Reserva> reservasEnRango = reservaRepo.findReservasEnRango(h.idHabitacion, desde, hasta);
            List<Estadia> estadiasEnRango = estadiaRepo.findEstadiasEnRango(h.idHabitacion, desdeTime, hastaTime);

            // Generar celdas por día
            for (int i = 0; i < dias; i++) {
                DisponibilidadDiaDTO dia = new DisponibilidadDiaDTO();
                LocalDate fechaActual = desde.plusDays(i);
                dia.fecha = fechaActual;

                // --- LÓGICA DE PRIORIDAD DE ESTADOS ---

                // PRIORIDAD 1: Bloqueo Global (Mantenimiento)
                if (h.estado == EstadoHabitacion.MANTENIMIENTO || h.estado == EstadoHabitacion.FUERA_DE_SERVICIO) {
                    dia.estado = h.estado.name();
                } else {
                    // PRIORIDAD 2: Estadía (Check-In activo) -> "OCUPADA"
                    boolean hayEstadia = estadiasEnRango.stream().anyMatch(estadia -> {
                        LocalDate checkIn = estadia.getCheckIn().toLocalDate();
                        LocalDate checkOut = estadia.getCheckOut().toLocalDate();
                        // El día está ocupado si cae entre checkIn (inclusive) y checkOut (exclusive o inclusive según regla)
                        return !fechaActual.isBefore(checkIn) && !fechaActual.isAfter(checkOut);
                    });

                    if (hayEstadia) {
                        dia.estado = "OCUPADA";
                    } else {
                        // PRIORIDAD 3: Reserva -> "RESERVADA"
                        boolean hayReserva = reservasEnRango.stream().anyMatch(reserva ->
                                (fechaActual.isEqual(reserva.getFechaDesde()) || fechaActual.isAfter(reserva.getFechaDesde())) &&
                                        (fechaActual.isEqual(reserva.getFechaHasta()) || fechaActual.isBefore(reserva.getFechaHasta())) &&
                                        "RESERVADA".equalsIgnoreCase(reserva.getEstado())
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