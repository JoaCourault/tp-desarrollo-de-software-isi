package com.isi.desa.Controller;

import com.isi.desa.Dto.Habitacion.HabitacionDisponibilidadDTO;
import com.isi.desa.Dto.Reserva.CrearReservaRequestDTO;
import com.isi.desa.Service.Interfaces.IReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/Reserva")
@CrossOrigin(origins = "http://localhost:3000")
public class ReservaController {

    @Autowired
    private IReservaService reservaService;

    @GetMapping("/Disponibilidad")
    public List<HabitacionDisponibilidadDTO> consultarDisponibilidad(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) String tipoHabitacion
    ) {
        return reservaService.obtenerDisponibilidad(desde, hasta, tipoHabitacion);
    }

    @PostMapping("/Crear")
    public ResponseEntity<?> crearReserva(@RequestBody CrearReservaRequestDTO request) {
        try {
            reservaService.crear(request);
            return ResponseEntity.ok("Reserva creada con éxito");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}