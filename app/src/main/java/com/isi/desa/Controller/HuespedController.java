package com.isi.desa.Controller;

import com.isi.desa.Dto.Huesped.*;
import com.isi.desa.Dto.Resultado;
import com.isi.desa.Exceptions.Huesped.CannotCreateHuespedException;
import com.isi.desa.Exceptions.Huesped.HuespedDuplicadoException;
import com.isi.desa.Service.Implementations.HuespedService;
import com.isi.desa.Service.Interfaces.ILogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Huesped")
// ¡SIN @CrossOrigin AQUI! (Ya está en CorsConfig)
public class HuespedController {

    @Autowired
    private HuespedService service;

    @Autowired
    private ILogger logger;

    @PostMapping("/Buscar")
    public BuscarHuespedResultDTO buscar(@RequestBody BuscarHuespedRequestDTO requestDTO) {
        try {
            return this.service.buscarHuesped(requestDTO);
        } catch (Exception e) {
            this.logger.error("Error buscar: " + e.getMessage(), e);
            BuscarHuespedResultDTO res = new BuscarHuespedResultDTO();
            res.resultado = new Resultado();
            res.resultado.id = 1;
            res.resultado.mensaje = "Error interno.";
            return res;
        }
    }

    @PostMapping("/Alta")
    public AltaHuespedResultDTO altaHuesped(@RequestBody AltaHuesperRequestDTO requestDTO) {
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
            // ERROR DE VALIDACIÓN (Falta campo, mal formato, etc.)
            res.resultado.id = 2;
            res.resultado.mensaje = e.getMessage();

        } catch (HuespedDuplicadoException e) {
            // 🔥 CAMBIO CLAVE: Usamos ID 3 para duplicados
            res.resultado.id = 3;
            res.resultado.mensaje = e.getMessage();

        } catch (Exception e) {
            this.logger.error("Error en altaHuesped: " + e.getMessage(), e);
            res.resultado.id = 1;
            res.resultado.mensaje = "Ocurrio un error interno: " + e.getMessage();
        }

        return res;
    }

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

    @PostMapping("/Baja")
    public BajaHuespedResultDTO bajaHuesped(@RequestBody BajaHuespedRequestDTO requestDTO) {
        try {
            return this.service.eliminar(requestDTO);
        } catch (Exception e){
            this.logger.error("Error baja: " + e.getMessage(), e);
            BajaHuespedResultDTO res = new BajaHuespedResultDTO();
            res.resultado = new Resultado();
            res.resultado.id = 1;
            res.resultado.mensaje = "Error interno";
            return res;
        }
    }
}