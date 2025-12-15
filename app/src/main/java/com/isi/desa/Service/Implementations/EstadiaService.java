package com.isi.desa.Service.Implementations;

import com.isi.desa.Dao.Implementations.EstadiaDAO;
import com.isi.desa.Dao.Implementations.ReservaDAO;
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
import java.util.UUID;

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

    @Autowired
    private ReservaDAO reservaDAO;

    @Transactional
    public EstadiaDTO ocuparHabitacion(CrearEstadiaRequestDTO request) {
        // 1. Validaciones básicas
        if (request.getIdsHabitaciones() == null || request.getIdsHabitaciones().isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos una habitación.");
        }
        if (request.getIdsHuespedes() == null || request.getIdsHuespedes().isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos un huésped.");
        }

        // 2. Buscar Entidades
        List<Habitacion> habitaciones = habitacionRepository.findAllById(request.getIdsHabitaciones());
        List<Huesped> huespedes = huespedRepository.findAllById(request.getIdsHuespedes());

        if (habitaciones.size() != request.getIdsHabitaciones().size()) {
            throw new IllegalArgumentException("Alguna de las habitaciones no existe.");
        }

        // 3. Validar Disponibilidad y Actualizar Estado de Habitaciones
        for (Habitacion hab : habitaciones) {
            // Si la habitación ya está ocupada, lanzamos error
            if (hab.getEstado() == EstadoHabitacion.OCUPADA || hab.getEstado() == EstadoHabitacion.FUERA_DE_SERVICIO) {
                throw new IllegalArgumentException("La habitación " + hab.getNumero() + " no está disponible.");
            }
            // Marcamos como ocupada
            hab.setEstado(EstadoHabitacion.OCUPADA);
            habitacionRepository.save(hab);
        }

        // 4. Instanciar Estadía
        Estadia estadia = new Estadia();

        // --- SOLUCIÓN CRÍTICA: SETEAR ID MANUALMENTE ---
        // Generamos un ID de máximo 20 caracteres para evitar el error de base de datos.
        // Al setearlo aquí (no null), Hibernate debería respetar este valor y no usar el generator uuid2.
        String uuidRaw = UUID.randomUUID().toString().replace("-", "");
        String idCorto = "ES_" + uuidRaw.substring(0, 15); // Total 18 caracteres (seguro < 20)
        estadia.setIdEstadia(idCorto);
        // -----------------------------------------------

        // Seteo de fechas
        estadia.setCheckIn(request.getCheckIn().atTime(14, 0));
        estadia.setCheckOut(request.getCheckOut().atTime(10, 0));
        estadia.setCantNoches(request.getCantNoches());

        // 5. LÓGICA DE RESERVA
        Reserva reservaVinculada;

        if (request.getIdReserva() != null && !request.getIdReserva().isEmpty()) {
            // CASO A: Viene con reserva previa
            reservaVinculada = reservaRepository.findById(request.getIdReserva())
                    .orElseThrow(() -> new RuntimeException("Reserva no encontrada: " + request.getIdReserva()));
        } else {
            // CASO B: WALK-IN (Sin reserva)
            System.out.println(">>> Generando Reserva Automática (Walk-In)...");

            reservaVinculada = new Reserva();

            // También generamos ID corto para la reserva por si acaso
            String uuidRes = UUID.randomUUID().toString().replace("-", "");
            reservaVinculada.setIdReserva("RE_" + uuidRes.substring(0, 15));

            reservaVinculada.setFechaIngreso(estadia.getCheckIn());
            reservaVinculada.setFechaEgreso(estadia.getCheckOut());
            reservaVinculada.setHabitacion(habitaciones.get(0));

            if (!huespedes.isEmpty()) {
                Huesped titular = huespedes.get(0);
                reservaVinculada.setNombreHuesped(titular.getNombre());
                reservaVinculada.setApellidoHuesped(titular.getApellido());
                reservaVinculada.setTelefonoHuesped(titular.getTelefono());
            } else {
                reservaVinculada.setApellidoHuesped("WALK-IN ANÓNIMO");
            }

            reservaVinculada = reservaDAO.guardar(reservaVinculada);
        }

        estadia.setReserva(reservaVinculada);

        // 6. Calcular Valor Total
        BigDecimal total = BigDecimal.ZERO;
        for (Habitacion h : habitaciones) {
            if (h.getPrecio() != null) {
                total = total.add(h.getPrecio());
            }
        }
        total = total.multiply(new BigDecimal(request.getCantNoches()));
        estadia.setValorTotalEstadia(total);

        // 7. Setear Relaciones
        estadia.setHabitaciones(habitaciones);
        estadia.setHuespedesHospedados(huespedes);

        // 8. Guardar Estadía
        // Al tener el ID ya seteado, el DAO insertará el string corto que generamos arriba.
        return estadiaDAO.save(estadia);
    }
}