package com.isi.desa.Controller;

import com.isi.desa.Dto.Habitacion.HabitacionDisponibilidadDTO;
import com.isi.desa.Dto.Reserva.CrearReservaRequestDTO;
import com.isi.desa.Service.Interfaces.IHabitacionService;
import com.isi.desa.Service.Interfaces.IReservaService; // <--- Importar la Interfaz
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
    private IHabitacionService habitacionService;

    @Autowired
    private IReservaService reservaService; // <--- Inyectamos la Interfaz, no la clase

    @GetMapping("/Disponibilidad")
    public List<HabitacionDisponibilidadDTO> consultarDisponibilidad(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return habitacionService.obtenerDisponibilidad(desde, hasta);
    }

    @PostMapping("/Crear")
    public ResponseEntity<?> crearReserva(@RequestBody CrearReservaRequestDTO request) {
        try {
            // Delegamos la lógica al servicio
            reservaService.crear(request);
            return ResponseEntity.ok("Reserva creada con éxito");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}