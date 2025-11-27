package com.isi.desa.Controller;

import com.isi.desa.Dto.Habitacion.HabitacionDisponibilidadDTO;
import com.isi.desa.Dto.Reserva.CrearReservaRequestDTO;
import com.isi.desa.Model.Entities.Reserva.Reserva;
import com.isi.desa.Service.Interfaces.IHabitacionService;
import com.isi.desa.Service.Interfaces.IReservaService;
import com.isi.desa.Dao.Repositories.ReservaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/Reserva")
@CrossOrigin(origins = "http://localhost:3000")
public class ReservaController {

    @Autowired
    private IHabitacionService habitacionService;

    @Autowired
    private IReservaService reservaService;

    @Autowired
    private ReservaRepository reservaRepo;

    // ===========================================================
    // 1) CONSULTAR DISPONIBILIDAD (YA EXISTENTE)
    // ===========================================================
    @GetMapping("/Disponibilidad")
    public List<HabitacionDisponibilidadDTO> consultarDisponibilidad(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        return habitacionService.obtenerDisponibilidad(desde, hasta);
    }

    // ===========================================================
    // 2) CREAR RESERVA (MEJORADO)
    // ===========================================================
    @PostMapping("/Crear")
    public ResponseEntity<?> crearReserva(@RequestBody CrearReservaRequestDTO request) {
        try {
            reservaService.crear(request);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Reserva creada con éxito"
            ));

        } catch (RuntimeException e) {
            // ERRORES DE NEGOCIO (ej: habitación reservada)
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));

        } catch (Exception e) {
            // ERRORES INTERNOS
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Error inesperado: " + e.getMessage()
            ));
        }
    }

    // ===========================================================
    // 3) NUEVO ENDPOINT - DEVOLVER FECHAS OCUPADAS POR HABITACIÓN
    // ===========================================================
    @GetMapping("/FechasOcupadas/{idHabitacion}")
    public ResponseEntity<?> obtenerFechasOcupadas(@PathVariable String idHabitacion) {

        List<Reserva> reservas = reservaRepo.findByHabitacion_IdHabitacion(idHabitacion);

        List<Map<String, LocalDate>> bloques = reservas.stream()
                .map(r -> Map.of(
                        "desde", r.getFechaDesde(),
                        "hasta", r.getFechaHasta()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "habitacion", idHabitacion,
                "bloqueos", bloques
        ));
    }
}
