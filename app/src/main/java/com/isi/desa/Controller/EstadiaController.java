package com.isi.desa.Controller;

import com.isi.desa.Dto.Estadia.CheckInRequestDTO;
import com.isi.desa.Dto.Resultado;
import com.isi.desa.Service.Interfaces.IEstadiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Estadia")
@CrossOrigin(origins = "http://localhost:3000")
public class EstadiaController {

    @Autowired
    private IEstadiaService estadiaService;

    @PostMapping("/CheckIn")
    public ResponseEntity<Resultado> realizarCheckIn(@RequestBody CheckInRequestDTO request) {
        Resultado resultado = new Resultado();
        try {
            estadiaService.realizarCheckIn(request);

            resultado.id = 0;
            resultado.mensaje = "Check-In realizado con éxito. Habitaciones ocupadas.";
            return ResponseEntity.ok(resultado);

        } catch (Exception e) {
            e.printStackTrace();
            resultado.id = 1;
            resultado.mensaje = "Error al realizar Check-In: " + e.getMessage();
            return ResponseEntity.internalServerError().body(resultado);
        }
    }
}