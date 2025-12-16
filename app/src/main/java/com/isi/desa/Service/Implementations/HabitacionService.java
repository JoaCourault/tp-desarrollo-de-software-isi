package com.isi.desa.Service.Implementations;

import com.isi.desa.Dao.Interfaces.IHabitacionDAO;
import com.isi.desa.Dao.Repositories.EstadiaRepository;
import com.isi.desa.Dao.Repositories.ReservaRepository;
import com.isi.desa.Dto.Habitacion.DisponibilidadDiaDTO;
import com.isi.desa.Dto.Habitacion.HabitacionDTO;
import com.isi.desa.Dto.Habitacion.HabitacionDisponibilidadDTO;
import com.isi.desa.Model.Entities.Estadia.Estadia;
import com.isi.desa.Model.Entities.Reserva.Reserva;
import com.isi.desa.Model.Enums.EstadoHabitacion;
import com.isi.desa.Service.Interfaces.IHabitacionService;
import com.isi.desa.Service.Interfaces.Validators.IHabitacionValidator;
import com.isi.desa.Utils.Mappers.HabitacionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HabitacionService implements IHabitacionService {

    @Autowired private IHabitacionDAO dao;
    @Autowired private IHabitacionValidator validator;
    @Autowired private ReservaRepository reservaRepo;
    @Autowired private EstadiaRepository estadiaRepo;

    @Override
    @Transactional
    public HabitacionDTO crear(HabitacionDTO dto) {

        RuntimeException err = validator.validateCreate(dto);
        if (err != null) throw err;

        boolean duplicada = dao.listar().stream().anyMatch(h ->
                h.getNumero() != null && dto.numero != null &&
                        h.getPiso() != null && dto.piso != null &&
                        h.getNumero().equals(dto.numero) &&
                        h.getPiso().equals(dto.piso)
        );
        if (duplicada) throw new RuntimeException("Ya existe una habitación con ese número y piso.");

        if (dto.idHabitacion == null || dto.idHabitacion.isBlank()) {
            String uuidRaw = UUID.randomUUID().toString().replace("-", "");
            dto.idHabitacion = "HA_" + uuidRaw.substring(0, 15);
        } else if (dao.existsById(dto.idHabitacion)) {
            throw new RuntimeException("Ya existe una habitación con el ID: " + dto.idHabitacion);
        }

        var entity = HabitacionMapper.dtoToEntity(dto);
        var saved = dao.save(entity);

        return HabitacionMapper.entityToDTO(saved);
    }

    @Override
    @Transactional
    public HabitacionDTO modificar(HabitacionDTO dto) {

        RuntimeException err = validator.validateUpdate(dto);
        if (err != null) throw err;

        if (!dao.existsById(dto.idHabitacion)) {
            throw new RuntimeException("No se encontró habitación con ID: " + dto.idHabitacion);
        }

        boolean duplicada = dao.listar().stream().anyMatch(h ->
                h.getIdHabitacion() != null &&
                        !h.getIdHabitacion().equalsIgnoreCase(dto.idHabitacion) &&
                        h.getNumero() != null && dto.numero != null &&
                        h.getPiso() != null && dto.piso != null &&
                        h.getNumero().equals(dto.numero) &&
                        h.getPiso().equals(dto.piso)
        );
        if (duplicada) throw new RuntimeException("Ya existe otra habitación con ese número y piso.");

        var entity = HabitacionMapper.dtoToEntity(dto);
        var saved = dao.save(entity);

        return HabitacionMapper.entityToDTO(saved);
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

        List<HabitacionDTO> todas = this.listar();

        Map<String, HabitacionDTO> mapaUnico = todas.stream()
                .filter(h -> h != null && h.idHabitacion != null)
                .collect(Collectors.toMap(
                        h -> h.idHabitacion,
                        h -> h,
                        (existente, nuevo) -> existente
                ));

        List<HabitacionDTO> habitacionesUnicas = new ArrayList<>(mapaUnico.values());
        habitacionesUnicas.sort(Comparator.comparing(h -> h.numero, Comparator.nullsLast(Integer::compareTo)));

        List<HabitacionDisponibilidadDTO> resultado = new ArrayList<>();

        long dias = ChronoUnit.DAYS.between(desde, hasta) + 1;

        LocalDateTime desdeTime = desde.atStartOfDay();
        LocalDateTime hastaTime = hasta.atTime(LocalTime.MAX);

        for (HabitacionDTO h : habitacionesUnicas) {

            HabitacionDisponibilidadDTO fila = new HabitacionDisponibilidadDTO();
            fila.habitacion = h;
            fila.disponibilidad = new ArrayList<>();

            // ✅ ACÁ estaba tu error: ahora va LocalDateTime
            List<Reserva> reservasEnRango = reservaRepo.findReservasEnRango(h.idHabitacion, desdeTime, hastaTime);

            // ✅ idem con EstadiaRepo (firma: (idHabitacion, desdeTime, hastaTime))
            List<Estadia> estadiasEnRango = estadiaRepo.findEstadiasEnRango(h.idHabitacion, desdeTime, hastaTime);

            for (int i = 0; i < dias; i++) {
                LocalDate fechaActual = desde.plusDays(i);

                DisponibilidadDiaDTO dia = new DisponibilidadDiaDTO();
                dia.fecha = fechaActual;

                if (h.estado == EstadoHabitacion.FUERA_DE_SERVICIO) {
                    dia.estado = EstadoHabitacion.FUERA_DE_SERVICIO.name();
                    fila.disponibilidad.add(dia);
                    continue;
                }

                // ESTADÍA: [checkIn, checkOut)
                boolean hayEstadia = estadiasEnRango.stream().anyMatch(estadia -> {
                    LocalDate checkIn = estadia.getCheckIn().toLocalDate();
                    LocalDate checkOut = estadia.getCheckOut().toLocalDate();
                    return !fechaActual.isBefore(checkIn) && fechaActual.isBefore(checkOut);
                });

                if (hayEstadia) {
                    dia.estado = "OCUPADA";
                } else {
                    // RESERVA: [ingreso, egreso) + estado RESERVADA
                    boolean hayReserva = reservasEnRango.stream().anyMatch(reserva -> {
                        if (!"RESERVADA".equalsIgnoreCase(reserva.getEstado())) return false;

                        LocalDate rDesde = reserva.getFechaDesde(); // transient -> LocalDate
                        LocalDate rHasta = reserva.getFechaHasta(); // transient -> LocalDate
                        if (rDesde == null || rHasta == null) return false;

                        return !fechaActual.isBefore(rDesde) && fechaActual.isBefore(rHasta);
                    });

                    dia.estado = hayReserva ? "RESERVADA" : "DISPONIBLE";
                }

                fila.disponibilidad.add(dia);
            }

            resultado.add(fila);
        }

        return resultado;
    }
}
