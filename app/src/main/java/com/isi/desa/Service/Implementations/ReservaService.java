package com.isi.desa.Service.Implementations;

import com.isi.desa.Dao.Interfaces.IHabitacionDAO;
import com.isi.desa.Dao.Interfaces.IReservaDAO;
import com.isi.desa.Dao.Repositories.EstadiaRepository;
import com.isi.desa.Dao.Repositories.HabitacionRepository;
import com.isi.desa.Dao.Repositories.ReservaRepository;
import com.isi.desa.Dto.Habitacion.DisponibilidadDiaDTO;
import com.isi.desa.Dto.Habitacion.HabitacionDTO;
import com.isi.desa.Dto.Habitacion.HabitacionDisponibilidadDTO;
import com.isi.desa.Dto.Reserva.CrearReservaRequestDTO;
import com.isi.desa.Dto.Reserva.ReservaDTO;
import com.isi.desa.Dto.Reserva.ReservaDetalleDTO;
import com.isi.desa.Model.Entities.Estadia.Estadia;
import com.isi.desa.Model.Entities.Habitacion.Habitacion;
import com.isi.desa.Model.Entities.Reserva.Reserva;
import com.isi.desa.Model.Enums.EstadoHabitacion;
import com.isi.desa.Service.Interfaces.IReservaService;
import com.isi.desa.Service.Interfaces.Validators.IReservaValidator;
import com.isi.desa.Utils.Mappers.HabitacionMapper;
import com.isi.desa.Utils.Mappers.ReservaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReservaService implements IReservaService {

    @Autowired
    private IReservaValidator validator;

    @Autowired
    @Qualifier("reservaDAO")
    private IReservaDAO reservaDAO;

    @Autowired
    private ReservaRepository reservaRepo;

    @Autowired
    private HabitacionRepository habitacionRepo;

    @Autowired
    private IHabitacionDAO habitacionDAO;

    @Autowired
    private EstadiaRepository estadiaRepo;

    // =========================================================================
    // CREAR RESERVA
    // =========================================================================
    @Override
    @Transactional
    public void crear(CrearReservaRequestDTO request) {

        validator.validateCreate(request);

        for (ReservaDetalleDTO detalle : request.reservas) {

            ReservaDTO dto = new ReservaDTO();
            dto.nombreCliente = request.nombreCliente;
            dto.apellidoCliente = request.apellidoCliente;
            dto.telefonoCliente = request.telefonoCliente;

            dto.idHabitacion = detalle.idHabitacion;
            dto.fechaDesde = detalle.fechaDesde;
            dto.fechaHasta = detalle.fechaHasta;

            if (detalle.fechaDesde != null) {
                dto.fechaIngreso = detalle.fechaDesde.atTime(14, 0);
            }
            if (detalle.fechaHasta != null) {
                dto.fechaEgreso = detalle.fechaHasta.atTime(10, 0);
            }

            dto.estado = "RESERVADA";

            Reserva reserva = ReservaMapper.dtoToEntity(dto);

            if (reserva.getIdReserva() == null || reserva.getIdReserva().isBlank()) {
                String uuidRes = UUID.randomUUID().toString().replace("-", "");
                reserva.setIdReserva("RE_" + uuidRes.substring(0, 15));
            }

            Habitacion hab = habitacionRepo.findById(detalle.idHabitacion)
                    .orElseThrow(() -> new RuntimeException("Habitación no encontrada: " + detalle.idHabitacion));
            reserva.setHabitacion(hab);

            reservaDAO.save(reserva);
        }
    }


    @Override
    @Transactional(readOnly = true)
    public List<HabitacionDisponibilidadDTO> obtenerDisponibilidad(LocalDate desde, LocalDate hasta, String tipoHabitacion) {

        // ... (Logica de obtención de habitaciones igual que antes) ...

        // 1. Obtener Habitaciones y Filtrar por Tipo
        List<HabitacionDTO> todas = habitacionDAO.listar().stream()
                .map(HabitacionMapper::entityToDTO)
                .collect(Collectors.toList());

        List<HabitacionDTO> habitacionesFiltradas = todas.stream()
                .filter(h -> h.idHabitacion != null)
                .collect(Collectors.toMap(h -> h.idHabitacion, h -> h, (old, n) -> old))
                .values().stream()
                .filter(h -> {
                    if (tipoHabitacion == null || tipoHabitacion.isBlank()) return true;
                    return h.tipoHabitacion != null && h.tipoHabitacion.toString().equalsIgnoreCase(tipoHabitacion.trim());
                })
                .sorted(Comparator.comparing(h -> h.numero, Comparator.nullsLast(Integer::compareTo)))
                .collect(Collectors.toList());

        List<HabitacionDisponibilidadDTO> resultado = new ArrayList<>();
        long dias = ChronoUnit.DAYS.between(desde, hasta) + 1;
        LocalDateTime desdeTime = desde.atStartOfDay();
        LocalDateTime hastaTime = hasta.atTime(LocalTime.MAX);

        // 2. Iterar Habitaciones
        for (HabitacionDTO h : habitacionesFiltradas) {

            HabitacionDisponibilidadDTO fila = new HabitacionDisponibilidadDTO();
            fila.habitacion = h;
            fila.disponibilidad = new ArrayList<>();

            // Buscar ocupación en DB
            List<Reserva> reservasTodas = reservaRepo.findReservasEnRango(h.idHabitacion, desdeTime, hastaTime);

            // Filtrar CANCELADA y EFECTIVIZADA ---
            List<Reserva> reservasActivas = reservasTodas.stream()
                    .filter(r -> {
                        String estado = r.getEstado();
                        if (estado == null) return true;
                        return !"CANCELADA".equalsIgnoreCase(estado) && !"EFECTIVIZADA".equalsIgnoreCase(estado);
                    })
                    .collect(Collectors.toList());

            List<Estadia> estadias = estadiaRepo.findEstadiasEnRango(h.idHabitacion, desdeTime, hastaTime);

            // 3. Iterar Días
            for (int i = 0; i < dias; i++) {
                LocalDate fechaActual = desde.plusDays(i);

                DisponibilidadDiaDTO dia = new DisponibilidadDiaDTO();
                dia.fecha = fechaActual;

                // --- 1. MANTENIMIENTO ---
                if (h.estado == EstadoHabitacion.FUERA_DE_SERVICIO) {
                    dia.estado = "MANTENIMIENTO";
                    fila.disponibilidad.add(dia);
                    continue;
                }

                // --- 2. ESTADÍAS (OCUPADA) ---
                // Si la reserva está EFECTIVIZADA, tendrá una estadía asociada que caerá en este 'if'.
                boolean hayEstadia = estadias.stream().anyMatch(e -> {
                    LocalDate checkIn = e.getCheckIn().toLocalDate();
                    LocalDate checkOut = (e.getCheckOut() != null) ? e.getCheckOut().toLocalDate() : LocalDate.MAX;
                    return !fechaActual.isBefore(checkIn) && !fechaActual.isAfter(checkOut);
                });

                if (hayEstadia) {
                    dia.estado = "OCUPADA";
                    fila.disponibilidad.add(dia);
                    continue;
                }


                boolean hayReserva = reservasActivas.stream().anyMatch(r -> {
                    LocalDate rDesde = r.getFechaDesde();
                    LocalDate rHasta = r.getFechaHasta();
                    if (rDesde == null || rHasta == null) return false;
                    return !fechaActual.isBefore(rDesde) && !fechaActual.isAfter(rHasta);
                });

                if (hayReserva) {
                    dia.estado = "RESERVADA";
                } else {
                    dia.estado = "DISPONIBLE";
                }

                fila.disponibilidad.add(dia);
            }
            resultado.add(fila);
        }

        return resultado;
    }
}