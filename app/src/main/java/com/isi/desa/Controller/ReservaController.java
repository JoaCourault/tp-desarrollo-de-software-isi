package com.isi.desa.Controller;

import com.isi.desa.Dto.Reserva.CrearReservaRequestDTO;
import com.isi.desa.Dto.Reserva.HabitacionDisponibilidadDTO;
import com.isi.desa.Dto.Resultado;
import com.isi.desa.Service.Implementations.ReservaService;
import com.isi.desa.Service.Interfaces.IReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/Reserva")
public class ReservaController {

    @Autowired
    private IReservaService reservaService;

    @PostMapping("/Crear")
    public Resultado crearReserva(@RequestBody CrearReservaRequestDTO request) {
        Resultado resultado = new Resultado();
        try {
            // Llamada al Gestor (Service)
            reservaService.realizarReserva(request);

            resultado.id = 0;
            resultado.mensaje = "Reserva realizada con éxito"; // Mensaje final del diagrama
        } catch (Exception e) {
            resultado.id = 1;
            resultado.mensaje = "Error al realizar reserva: " + e.getMessage();
        }
        return resultado;
    }

    @GetMapping("/Disponibilidad")
    public ResponseEntity<List<HabitacionDisponibilidadDTO>> obtenerDisponibilidad(
            @RequestParam("desde") String desdeStr,
            @RequestParam("hasta") String hastaStr) {

        try {
            LocalDate desde = LocalDate.parse(desdeStr);
            LocalDate hasta = LocalDate.parse(hastaStr);

            List<HabitacionDisponibilidadDTO> disponibilidad = reservaService.consultarDisponibilidad(desde, hasta);
            return ResponseEntity.ok(disponibilidad);

        } catch (Exception e) {
            e.printStackTrace(); // Ver error en consola
            return ResponseEntity.internalServerError().build();
        }
    }
}