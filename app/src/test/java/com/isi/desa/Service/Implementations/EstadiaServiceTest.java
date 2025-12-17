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
import com.isi.desa.Model.Entities.Habitacion.IndividualEstandar;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Model.Entities.Reserva.Reserva;
import com.isi.desa.Model.Enums.EstadoHabitacion;
import com.isi.desa.Model.Enums.EstadoReserva;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstadiaServiceTest {

    @InjectMocks
    private EstadiaService estadiaService;

    @Mock
    private EstadiaDAO estadiaDAO;
    @Mock
    private HabitacionRepository habitacionRepository;
    @Mock
    private HuespedRepository huespedRepository;
    @Mock
    private ReservaRepository reservaRepository;
    @Mock
    private ReservaDAO reservaDAO;

    /* =========================
       VALIDACIONES INICIALES
       ========================= */

    @Test
    void ocuparHabitacion_sinHabitaciones_error() {
        CrearEstadiaRequestDTO request = new CrearEstadiaRequestDTO();
        request.setIdsHabitaciones(List.of());

        assertThrows(IllegalArgumentException.class,
                () -> estadiaService.ocuparHabitacion(request));
    }

    @Test
    void ocuparHabitacion_sinTitular_error() {
        CrearEstadiaRequestDTO request = crearRequestBase();
        request.setIdHuespedTitular("");

        assertThrows(IllegalArgumentException.class,
                () -> estadiaService.ocuparHabitacion(request));
    }

    /* =========================
       HABITACION FUERA DE SERVICIO
       ========================= */

    @Test
    void ocuparHabitacion_habitacionFueraDeServicio_error() {
        CrearEstadiaRequestDTO request = crearRequestBase();

        Habitacion hab = crearHabitacion();
        hab.setEstado(EstadoHabitacion.FUERA_DE_SERVICIO);

        when(habitacionRepository.findAllById(any()))
                .thenReturn(List.of(hab));
        when(huespedRepository.findAllById(any()))
                .thenReturn(List.of(crearHuesped()));
        when(huespedRepository.findById(any()))
                .thenReturn(Optional.of(crearHuesped()));

        assertThrows(IllegalArgumentException.class,
                () -> estadiaService.ocuparHabitacion(request));
    }

    /* =========================
       WALK-IN (SIN RESERVA)
       ========================= */

    @Test
    void ocuparHabitacion_walkIn_ok() {
        CrearEstadiaRequestDTO request = crearRequestBase();
        request.setIdReserva(null);

        Habitacion hab = crearHabitacion();

        when(habitacionRepository.findAllById(any()))
                .thenReturn(List.of(hab));
        when(huespedRepository.findAllById(any()))
                .thenReturn(List.of(crearHuesped()));
        when(huespedRepository.findById(any()))
                .thenReturn(Optional.of(crearHuesped()));
        when(estadiaDAO.save(any()))
                .thenReturn(new EstadiaDTO());

        EstadiaDTO result = estadiaService.ocuparHabitacion(request);

        assertNotNull(result);
    }

    /* =========================
       CON RESERVA
       ========================= */

    @Test
    void ocuparHabitacion_conReserva_ok() {
        CrearEstadiaRequestDTO request = crearRequestBase();
        request.setIdReserva("RES1");

        Habitacion hab = crearHabitacion();
        Reserva reserva = new Reserva();
        reserva.setEstado(EstadoReserva.EFECTIVIZADA);

        when(habitacionRepository.findAllById(any()))
                .thenReturn(List.of(hab));
        when(huespedRepository.findAllById(any()))
                .thenReturn(List.of(crearHuesped()));
        when(huespedRepository.findById(any()))
                .thenReturn(Optional.of(crearHuesped()));
        when(reservaRepository.findById("RES1"))
                .thenReturn(Optional.of(reserva));
        when(estadiaDAO.save(any()))
                .thenReturn(new EstadiaDTO());

        EstadiaDTO result = estadiaService.ocuparHabitacion(request);

        assertNotNull(result);
        assertEquals(EstadoReserva.EFECTIVIZADA, reserva.getEstado());
    }

    /* =========================
       HELPERS
       ========================= */

    private CrearEstadiaRequestDTO crearRequestBase() {
        CrearEstadiaRequestDTO dto = new CrearEstadiaRequestDTO();
        dto.setIdsHabitaciones(List.of("H1"));
        dto.setIdsHuespedes(List.of("HU1"));
        dto.setIdHuespedTitular("HU1");
        dto.setCheckIn(LocalDate.now());
        dto.setCheckOut(LocalDate.now().plusDays(1));
        dto.setCantNoches(1);
        return dto;
    }

    private Habitacion crearHabitacion() {
        Habitacion h = new IndividualEstandar();
        h.setEstado(EstadoHabitacion.DISPONIBLE);
        h.setPrecio(BigDecimal.valueOf(100));
        h.setNumero(1);
        return h;
    }

    private Huesped crearHuesped() {
        Huesped h = new Huesped();
        h.setIdHuesped("HU1");
        return h;
    }
}
