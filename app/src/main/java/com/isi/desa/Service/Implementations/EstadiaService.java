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
import com.isi.desa.Model.Enums.EstadoHabitacion;
import com.isi.desa.Service.Interfaces.IEstadiaService;
import com.isi.desa.Service.Interfaces.Validators.IEstadiaValidator;
import com.isi.desa.Utils.Mappers.EstadiaMapper;
import com.isi.desa.Utils.Mappers.HabitacionMapper;
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

        // 1. Validaciones básicas
        if (request == null || request.habitaciones == null || request.habitaciones.isEmpty()) {
            throw new RuntimeException("No se enviaron datos de habitaciones para el check-in.");
        }
        if (request.idHuespedTitular == null || request.idHuespedTitular.trim().isEmpty()) {
            throw new RuntimeException("El huésped titular es obligatorio para realizar el check-in.");
        }

        // 2. Construcción del DTO preliminar
        EstadiaDTO estadiaDTO = new EstadiaDTO();
        estadiaDTO.idHuespedTitular = request.idHuespedTitular;

        HabitacionCheckInDTO primeraHab = request.habitaciones.get(0);
        if (primeraHab == null || primeraHab.fechaDesde == null) {
            throw new RuntimeException("La fechaDesde (check-in) es obligatoria.");
        }
        if (primeraHab.fechaHasta == null) {
            // si no vino, asumimos 1 noche
            primeraHab.fechaHasta = primeraHab.fechaDesde.plusDays(1);
        }

        estadiaDTO.checkIn = primeraHab.fechaDesde;
        estadiaDTO.checkOut = primeraHab.fechaHasta;

        long nochesCalc = ChronoUnit.DAYS.between(
                primeraHab.fechaDesde.toLocalDate(),
                primeraHab.fechaHasta.toLocalDate()
        );
        estadiaDTO.cantNoches = (int) (nochesCalc <= 0 ? 1 : nochesCalc);

        Set<String> todosLosOcupantesIds = new HashSet<>();
        todosLosOcupantesIds.add(request.idHuespedTitular);

        for (HabitacionCheckInDTO hab : request.habitaciones) {
            if (hab != null && hab.acompanantesIds != null) {
                todosLosOcupantesIds.addAll(hab.acompanantesIds);
            }
        }
        estadiaDTO.idsOcupantes = new ArrayList<>(todosLosOcupantesIds);
        estadiaDTO.valorTotalEstadia = BigDecimal.ZERO;

        // 3. Validación de Negocio
        RuntimeException errorValidacion = validator.validateCreate(estadiaDTO);
        if (errorValidacion != null) {
            throw errorValidacion;
        }

        // --- CONSTRUCCIÓN DE ENTIDAD ---
        Estadia estadiaEntity = EstadiaMapper.dtoToEntity(estadiaDTO);
        long count = estadiaRepository.count();
        estadiaEntity.setIdEstadia(String.format("EST-%03d", count + 1));

        // Asignar Titular
        Huesped titularEntity = huespedDAO.getById(request.idHuespedTitular);
        if (titularEntity == null) {
            throw new RuntimeException("El huésped titular con ID " + request.idHuespedTitular + " no existe.");
        }
        estadiaEntity.setHuespedTitular(titularEntity);

        // --- PROCESAMIENTO DE HABITACIONES Y CÁLCULO DE COSTO ---
        BigDecimal valorTotalAcumulado = BigDecimal.ZERO;

        Reserva reservaEncontrada = null;
        List<Habitacion> habitacionesParaEstadia = new ArrayList<>();

        for (HabitacionCheckInDTO habDTO : request.habitaciones) {
            if (habDTO == null || habDTO.idHabitacion == null) continue;

            if (habDTO.fechaDesde == null) {
                throw new RuntimeException("fechaDesde es obligatoria para la habitación " + habDTO.idHabitacion);
            }
            if (habDTO.fechaHasta == null) {
                habDTO.fechaHasta = habDTO.fechaDesde.plusDays(1);
            }

            Habitacion habitacion = habitacionDAO.obtener(habDTO.idHabitacion);
            if (habitacion == null) {
                throw new RuntimeException("Habitación no encontrada: " + habDTO.idHabitacion);
            }

            // Gestión de Reserva (busca solapadas por TIMESTAMP)
            if (reservaEncontrada == null) {
                List<Reserva> reservas = reservaRepository.findReservasEnRango(
                        habDTO.idHabitacion,
                        habDTO.fechaDesde,
                        habDTO.fechaHasta
                );

                Reserva r = reservas.stream()
                        .filter(res -> "RESERVADA".equalsIgnoreCase(res.getEstado()))
                        .findFirst()
                        .orElse(null);

                if (r != null) {
                    r.setEstado("EFECTIVIZADA");
                    reservaDAO.update(ReservaMapper.entityToDTO(r));
                    reservaEncontrada = r;
                }
            }

            // Cálculo costo
            BigDecimal precioNoche = (habitacion.getPrecio() != null)
                    ? habitacion.getPrecio()
                    : BigDecimal.ZERO;

            BigDecimal costoHabitacion = precioNoche.multiply(new BigDecimal(estadiaDTO.cantNoches));
            valorTotalAcumulado = valorTotalAcumulado.add(costoHabitacion);

            // Cambiar estado habitación
            habitacion.setEstado(EstadoHabitacion.OCUPADA);
            habitacionDAO.modificar(HabitacionMapper.entityToDTO(habitacion));

            habitacionesParaEstadia.add(habitacion);
        }

        estadiaEntity.setValorTotalEstadia(valorTotalAcumulado);
        estadiaEntity.setReserva(reservaEncontrada);
        estadiaEntity.setListaHabitaciones(habitacionesParaEstadia);

        // Asignar Huéspedes (ManyToMany)
        List<Huesped> listaHuespedesEntidad = new ArrayList<>();
        for (String idHuesped : todosLosOcupantesIds) {
            Huesped h = huespedDAO.getById(idHuesped);
            if (h != null) listaHuespedesEntidad.add(h);
        }
        estadiaEntity.setListaHuespedes(listaHuespedesEntidad);

        // Guardar
        estadiaDAO.save(estadiaEntity);
    }
}
