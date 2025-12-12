package com.isi.desa.Controller;

import com.isi.desa.Dto.Reserva.CrearReservaRequestDTO;
import com.isi.desa.Dto.Reserva.HabitacionDisponibilidadDTO;
import com.isi.desa.Dto.Resultado;
import com.isi.desa.Service.Implementations.ReservaService;
import com.isi.desa.Service.Interfaces.IReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.isi.desa.Dto.Reserva.ReservaListadoDTO;
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
    // Endpoint: BUSCAR RESERVAS (GET)
    // URL: /Reserva/Buscar?apellido=Gomez&nombre=Juan
    @GetMapping("/Buscar")
    public ResponseEntity<?> buscarReservas(
            @RequestParam("apellido") String apellido,
            @RequestParam(value = "nombre", required = false) String nombre) {

        try {
            List<ReservaListadoDTO> resultado = reservaService.buscarParaCancelar(apellido, nombre);
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            // Errores de validación (campos vacíos, caracteres raros) -> 400 Bad Request
            return ResponseEntity.badRequest().body(new Resultado(1, e.getMessage()));
        } catch (RuntimeException e) {
            // Errores de lógica (no encontrado, etc) -> 409 Conflict o 404
            return ResponseEntity.status(404).body(new Resultado(1, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new Resultado(1, "Error interno del servidor"));
        }
    }

    // Endpoint: CANCELAR RESERVAS (DELETE)
    // Recibe una lista de IDs. Ej: ["UUID-1", "UUID-2"]
    @DeleteMapping("/Cancelar")
    public ResponseEntity<Resultado> cancelarReservas(@RequestBody List<String> idsReservas) {
        Resultado resultado = new Resultado();
        try {
            reservaService.cancelarReservas(idsReservas);

            resultado.id = 0;
            resultado.mensaje = "Reservas canceladas correctamente.";
            return ResponseEntity.ok(resultado);

        } catch (Exception e) {
            resultado.id = 1;
            resultado.mensaje = "Error al cancelar reservas: " + e.getMessage();
            return ResponseEntity.badRequest().body(resultado);
        }
    }
}