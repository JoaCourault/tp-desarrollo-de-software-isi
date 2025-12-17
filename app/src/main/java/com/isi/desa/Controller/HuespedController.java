package com.isi.desa.Controller;

import com.isi.desa.Dto.Huesped.*;
import com.isi.desa.Dto.Resultado;
import com.isi.desa.Exceptions.Huesped.CannotCreateHuespedException;
import com.isi.desa.Exceptions.Huesped.HuespedDuplicadoException;
import com.isi.desa.Service.Implementations.HuespedService;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Service.Interfaces.ILogger;
import com.isi.desa.Utils.Mappers.HuespedMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Huesped")
public class HuespedController {

    @Autowired
    private HuespedService service;

    @Autowired
    private ILogger logger;

    @Autowired
    private HuespedMapper huespedMapper; // 1.INYECTAMOS EL MAPPER

    // --- CU02 Pasos 1-4: Buscar con filtros ---
    @PostMapping("/Buscar")
    public BuscarHuespedResultDTO buscar(@RequestBody BuscarHuespedRequestDTO requestDTO) {
        try {
            return this.service.buscarHuesped(requestDTO);
        } catch (Exception e) {
            this.logger.error("Error buscar: " + e.getMessage(), e);
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
            // 2. USAMOS LA INSTANCIA (huespedMapper)
            return ResponseEntity.ok(huespedMapper.entityToDTO(huesped));
        } catch (Exception e) {
            this.logger.error("Error al obtener huesped por ID: " + e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // --- CU09: Alta de Huésped ---
    @PostMapping("/Alta")
    public AltaHuespedResultDTO altaHuesped(@RequestBody AltaHuespedRequestDTO requestDTO) {
        AltaHuespedResultDTO res = new AltaHuespedResultDTO();
        res.resultado = new Resultado();

        System.out.println("Llegó request: " + requestDTO);
        if (requestDTO.huesped != null) {
            System.out.println("Nombre: " + requestDTO.huesped.nombre);
            System.out.println("TipoDoc: " + requestDTO.huesped.tipoDoc);
        } else {
            System.out.println("EL OBJETO HUESPED ES NULL (Error de estructura JSON)");
        }

        try {
            HuespedDTO creado = this.service.crear(requestDTO.huesped, requestDTO.aceptarIgualmente);
            res.resultado.id = 0;
            res.resultado.mensaje = "Huesped dado de alta exitosamente.";
            res.huesped = creado;

        } catch (CannotCreateHuespedException e) {
            // errores de validación normales
            res.resultado.id = 2;
            res.resultado.mensaje = e.getMessage();

        } catch (HuespedDuplicadoException e) {
            //Usamos ID 3 para duplicados
            res.resultado.id = 3;
            res.resultado.mensaje = e.getMessage();

        } catch (Exception e) {
            // error inesperado
            this.logger.error("Error en altaHuesped: " + e.getMessage(), e);
            res.resultado.id = 1;
            res.resultado.mensaje = "Ocurrio un error interno: " + e.getMessage();
        }

        return res;
    }
    // --- CU10: Modificar Huésped ---
    @PostMapping("/Modificar")
    public ModificarHuespedResultDTO modificarHuesped(@RequestBody ModificarHuespedRequestDTO requestDTO) {
        try {
            return this.service.modificar(requestDTO);
        } catch (Exception e) {
            this.logger.error("Error modificar: " + e.getMessage(), e);
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
