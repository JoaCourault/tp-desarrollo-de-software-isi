package com.isi.desa.Service.Implementations;

import com.isi.desa.Dao.Implementations.EstadiaDAO;
import com.isi.desa.Dao.Repositories.HabitacionRepository;
import com.isi.desa.Dao.Repositories.HuespedRepository;
import com.isi.desa.Dto.Estadia.CrearEstadiaRequestDTO;
import com.isi.desa.Dto.Estadia.EstadiaDTO;
import com.isi.desa.Model.Entities.Estadia.Estadia;
import com.isi.desa.Model.Entities.Habitacion.Habitacion;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Model.Enums.EstadoHabitacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class EstadiaService {

    @Autowired
    private EstadiaDAO estadiaDAO;

    @Autowired
    private HabitacionRepository habitacionRepository;

    @Autowired
    private HuespedRepository huespedRepository;

    @Transactional
    public EstadiaDTO ocuparHabitacion(CrearEstadiaRequestDTO request) {
        // 1. Validaciones básicas
        if (request.getIdsHabitaciones() == null || request.getIdsHabitaciones().isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos una habitación.");
        }
        if (request.getIdsHuespedes() == null || request.getIdsHuespedes().isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos un huésped.");
        }

        // 2. Buscar Entidades (Habitaciones y Huéspedes)
        List<Habitacion> habitaciones = habitacionRepository.findAllById(request.getIdsHabitaciones());
        List<Huesped> huespedes = huespedRepository.findAllById(request.getIdsHuespedes());

        if (habitaciones.size() != request.getIdsHabitaciones().size()) {
            throw new IllegalArgumentException("Alguna de las habitaciones no existe.");
        }

        // 3. Validar Disponibilidad y Actualizar Estado
        for (Habitacion hab : habitaciones) {
            // Verifica que no esté ocupada ni fuera de servicio
            if (hab.getEstado() == EstadoHabitacion.OCUPADA || hab.getEstado() == EstadoHabitacion.FUERA_DE_SERVICIO) {
                throw new IllegalArgumentException("La habitación " + hab.getNumero() + " no está disponible.");
            }
            // Actualizamos estado a OCUPADA (Check-In efectivo)
            hab.setEstado(EstadoHabitacion.OCUPADA);
            habitacionRepository.save(hab);
        }

        // 4. Instanciar Estadía
        Estadia estadia = new Estadia();
        estadia.setIdEstadia("EST-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        // --- CONVERSIÓN DE FECHAS (LocalDate -> LocalDateTime) ---
        // Asignamos las 14:00 para Check-In y 10:00 para Check-Out por defecto
        estadia.setCheckIn(request.getCheckIn().atTime(14, 0));
        estadia.setCheckOut(request.getCheckOut().atTime(10, 0));

        estadia.setCantNoches(request.getCantNoches());

        if (request.getIdReserva() != null) {
            estadia.setIdReserva(request.getIdReserva());
        }

        // 5. Calcular Valor Total
        BigDecimal total = BigDecimal.ZERO;
        for (Habitacion h : habitaciones) {
            if (h.getPrecio() != null) {
                total = total.add(h.getPrecio());
            }
        }
        total = total.multiply(new BigDecimal(request.getCantNoches()));
        estadia.setValorTotalEstadia(total);

        // 6. Setear Relaciones ManyToMany
        estadia.setHabitaciones(habitaciones);
        estadia.setHuespedes(huespedes);

        // 7. Guardar
        return estadiaDAO.save(estadia);
    }
}