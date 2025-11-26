package com.isi.desa.Controller;

import com.isi.desa.Dto.Habitacion.HabitacionDTO;
import com.isi.desa.Service.Interfaces.IHabitacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Habitacion")
public class HabitacionController {

    @Autowired
    private IHabitacionService service;

    @GetMapping("/Listar")
    public List<HabitacionDTO> listar() {
        return service.listar();
    }

    @PostMapping("/Crear")
    public HabitacionDTO crear(@RequestBody HabitacionDTO dto) {
        return service.crear(dto);
    }

    @PostMapping("/Modificar")
    public HabitacionDTO modificar(@RequestBody HabitacionDTO dto) {
        return service.modificar(dto);
    }
}
