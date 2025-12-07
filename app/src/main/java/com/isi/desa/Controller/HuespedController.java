package com.isi.desa.Controller;

import com.isi.desa.Dto.Huesped.*;
import com.isi.desa.Dto.Resultado;
import com.isi.desa.Exceptions.Huesped.CannotCreateHuespedException;
import com.isi.desa.Exceptions.Huesped.HuespedDuplicadoException;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Service.Interfaces.IHuespedService;
import com.isi.desa.Service.Interfaces.ILogger;
import com.isi.desa.Utils.Mappers.HuespedMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Huesped")
public class HuespedController {

    @Autowired
    private IHuespedService service;

    @Autowired
    private ILogger logger;

    @Autowired
    private HuespedMapper huespedMapper; // <--- 1. INYECTAMOS EL MAPPER

    // --- CU02 Pasos 1-4: Buscar con filtros ---
    @PostMapping("/Buscar")
    public BuscarHuespedResultDTO buscar(@RequestBody BuscarHuespedRequestDTO requestDTO) {
        try {
            return this.service.buscarHuesped(requestDTO);
        } catch (Exception e) {
            this.logger.error("Error en buscarHuesped: " + e.getMessage(), e);
            BuscarHuespedResultDTO res = new BuscarHuespedResultDTO();
            res.resultado = new Resultado();
            res.resultado.id = 1;
            res.resultado.mensaje = "Error interno al buscar huéspedes.";
            return res;
        }
    }

    // --- CU02 Paso 5: Traer Huésped seleccionado ---
    @GetMapping("/{id}")
    public ResponseEntity<HuespedDTO> obtenerPorId(@PathVariable String id) {
        try {
            Huesped huesped = this.service.getById(id);
            if (huesped == null) {
                return ResponseEntity.notFound().build();
            }
            // 2. USAMOS LA INSTANCIA (huespedMapper), NO LA CLASE
            return ResponseEntity.ok(huespedMapper.entityToDTO(huesped));
        } catch (Exception e) {
            this.logger.error("Error al obtener huesped por ID: " + e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // --- CU09: Alta de Huésped ---
    @PostMapping("/Alta")
    public AltaHuespedResultDTO altaHuesped(@RequestBody AltaHuesperRequestDTO requestDTO) {
        AltaHuespedResultDTO res = new AltaHuespedResultDTO();
        res.resultado = new Resultado();

        try {
            // Asumimos que tu interfaz IHuespedService ya soporta la sobrecarga o casteamos
            // Si la interfaz no tiene el método con booleano, usa el servicio concreto o actualiza la interfaz
            HuespedDTO creado = ((com.isi.desa.Service.Implementations.HuespedService)this.service)
                    .crear(requestDTO.huesped, requestDTO.aceptarIgualmente);

            res.resultado.id = 0;
            res.resultado.mensaje = "Huesped dado de alta exitosamente.";
            res.huesped = creado;

        } catch (HuespedDuplicadoException e) {
            res.resultado.id = 2; // Código para advertencia en front
            res.resultado.mensaje = e.getMessage();
        } catch (CannotCreateHuespedException e) {
            res.resultado.id = 1;
            res.resultado.mensaje = e.getMessage();
        } catch (Exception e) {
            this.logger.error("Error en altaHuesped: " + e.getMessage(), e);
            res.resultado.id = 1;
            res.resultado.mensaje = "Ocurrio un error interno.";
        }
        return res;
    }

    // --- CU10: Modificar Huésped ---
    @PutMapping("/Modificar")
    public ModificarHuespedResultDTO modificarHuesped(@RequestBody ModificarHuespedRequestDTO requestDTO) {
        try {
            return this.service.modificar(requestDTO);
        } catch (Exception e) {
            this.logger.error("Error en HuespedController - modificarHuesped: " + e.getMessage(), e);
            ModificarHuespedResultDTO res = new ModificarHuespedResultDTO();
            res.resultado = new Resultado();
            res.resultado.id = 1;
            res.resultado.mensaje = "Error interno";
            return res;
        }
    }

    // --- CU11: Baja Huésped ---
    @PostMapping("/Baja")
    public BajaHuespedResultDTO bajaHuesped(@RequestBody BajaHuespedRequestDTO requestDTO) {
        try {
            return this.service.eliminar(requestDTO);
        } catch (Exception e){
            this.logger.error("Error en HuespedController - bajaHuesped: " + e.getMessage(), e);
            BajaHuespedResultDTO res = new BajaHuespedResultDTO();
            res.resultado = new Resultado();
            res.resultado.id = 1;
            res.resultado.mensaje = "Error al dar de baja";
            return res;
        }
    }
}