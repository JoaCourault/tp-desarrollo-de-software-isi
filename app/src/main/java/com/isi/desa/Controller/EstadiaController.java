package com.isi.desa.Controller;

import com.isi.desa.Dto.Estadia.CrearEstadiaRequestDTO;
import com.isi.desa.Dto.Estadia.EstadiaDTO;
import com.isi.desa.Dto.Resultado;
import com.isi.desa.Service.Implementations.EstadiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Estadia")
public class EstadiaController {

    @Autowired
    private EstadiaService estadiaService;

    @PostMapping("/CheckIn")
    public ResponseEntity<?> realizarCheckIn(@RequestBody CrearEstadiaRequestDTO request) {
        try {
            EstadiaDTO estadiaCreada = estadiaService.ocuparHabitacion(request);
            return ResponseEntity.ok(estadiaCreada);
        } catch (IllegalArgumentException e) {
            // Error de validación (habitación ocupada, etc.)
            Resultado error = new Resultado();
            error.id = 1;
            error.mensaje = e.getMessage();
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error interno al realizar Check-In");
        }
    }
}