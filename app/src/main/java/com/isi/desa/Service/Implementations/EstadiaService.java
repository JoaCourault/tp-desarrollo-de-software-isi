package com.isi.desa.Service.Implementations;

import com.isi.desa.Dao.Implementations.EstadiaDAO;
import com.isi.desa.Dao.Repositories.HabitacionRepository;
import com.isi.desa.Dao.Repositories.HuespedRepository;
import com.isi.desa.Dao.Repositories.ReservaRepository;
import com.isi.desa.Dto.Estadia.CrearEstadiaRequestDTO;
import com.isi.desa.Dto.Estadia.EstadiaDTO;
import com.isi.desa.Model.Entities.Estadia.Estadia;
import com.isi.desa.Model.Entities.Habitacion.Habitacion;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Model.Entities.Reserva.Reserva;
import com.isi.desa.Model.Enums.EstadoHabitacion;
import com.isi.desa.Service.Interfaces.IEstadiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class EstadiaService implements IEstadiaService {

    @Autowired
    private EstadiaDAO estadiaDAO;

    @Autowired
    private HabitacionRepository habitacionRepository;

    @Autowired
    private HuespedRepository huespedRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Override
    @Transactional
    public EstadiaDTO ocuparHabitacion(CrearEstadiaRequestDTO request) {
        // 1. VALIDACIONES
        if (request.getIdsHabitaciones() == null || request.getIdsHabitaciones().isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos una habitación.");
        }
        if (request.getIdHuespedTitular() == null || request.getIdHuespedTitular().isBlank()) {
            throw new IllegalArgumentException("Es obligatorio designar un Titular para la estadía.");
        }

        // 2. RECUPERAR ENTIDADES
        List<Habitacion> habitaciones = habitacionRepository.findAllById(request.getIdsHabitaciones());
        List<Huesped> todosLosHuespedes = huespedRepository.findAllById(request.getIdsHuespedes());

        // Buscamos Titular existente
        Huesped titularEntity = huespedRepository.findById(request.getIdHuespedTitular())
                .orElseThrow(() -> new IllegalArgumentException("El titular seleccionado no existe en la base de datos."));

        if (habitaciones.size() != request.getIdsHabitaciones().size()) {
            throw new IllegalArgumentException("Alguna de las habitaciones solicitadas no existe.");
        }

        // 3. CAMBIAR ESTADO HABITACIONES
        for (Habitacion hab : habitaciones) {
            if (hab.getEstado() == EstadoHabitacion.OCUPADA || hab.getEstado() == EstadoHabitacion.FUERA_DE_SERVICIO) {
                throw new IllegalArgumentException("La habitación " + hab.getNumero() + " no está disponible.");
            }
            hab.setEstado(EstadoHabitacion.OCUPADA);
            habitacionRepository.save(hab);
        }

        // 4. CREAR ESTADÍA
        Estadia estadia = new Estadia();

        // (ID generado automáticamente por JPA)

        estadia.setCheckIn(request.getCheckIn().atTime(14, 0));
        estadia.setCheckOut(request.getCheckOut().atTime(10, 0));
        estadia.setCantNoches(request.getCantNoches());

        // Vinculamos Titular (columna id_huesped_titular)
        estadia.setHuesped(titularEntity);

        // 5. GESTIÓN DE RESERVA
        if (request.getIdReserva() != null && !request.getIdReserva().isEmpty()) {
            // CASO A: Viene de una Reserva Existente -> La vinculamos
            Reserva reservaVinculada = reservaRepository.findById(request.getIdReserva())
                    .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + request.getIdReserva()));

            estadia.setReserva(reservaVinculada);
        } else {
            // CASO B: Walk-In -> No hay reserva asociada
            estadia.setReserva(null);
        }

        // 6. VALOR TOTAL
        BigDecimal total = BigDecimal.ZERO;
        for (Habitacion h : habitaciones) {
            if (h.getPrecio() != null) {
                total = total.add(h.getPrecio());
            }
        }
        total = total.multiply(new BigDecimal(request.getCantNoches()));
        estadia.setValorTotalEstadia(total);

        // 7. RELACIONES
        estadia.setHabitaciones(habitaciones);
        estadia.setHuespedesHospedados(todosLosHuespedes);

        return estadiaDAO.save(estadia);
    }
}