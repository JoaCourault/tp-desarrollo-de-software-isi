package com.isi.desa.Controller;

import com.isi.desa.Dto.Huesped.*;
import com.isi.desa.Dto.Resultado;
import com.isi.desa.Exceptions.Huesped.CannotCreateHuespedException;
import com.isi.desa.Exceptions.Huesped.HuespedDuplicadoException;
import com.isi.desa.Service.Implementations.HuespedService;
import com.isi.desa.Service.Implementations.Logger;
import com.isi.desa.Service.Interfaces.IHuespedService;
import com.isi.desa.Service.Interfaces.ILogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/Huesped")
public class HuespedController {

    @Autowired
    private IHuespedService service;

    @Autowired
    private ILogger logger;

    // instancia única SINGLETON
    private static final HuespedController INSTANCE = new HuespedController();

    public static HuespedController getInstance() {
        return INSTANCE;
    }

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

    @PostMapping("/Alta")
    public AltaHuespedResultDTO altaHuesped(@RequestBody AltaHuespedRequestDTO requestDTO) {
        try {

            return this.service.crear(requestDTO);

        } catch (CannotCreateHuespedException e) {
            AltaHuespedResultDTO res = new AltaHuespedResultDTO();
            res.resultado = new Resultado();
            res.resultado.id = 2;
            res.resultado.mensaje = e.getMessage();
            return res;

        } catch (Exception e) {
            this.logger.error("Error en altaHuesped: " + e.getMessage(), e);
            AltaHuespedResultDTO res = new AltaHuespedResultDTO();
            res.resultado = new Resultado();
            res.resultado.id = 1;
            res.resultado.mensaje = "Ocurrió un error interno al realizar el alta del huésped.";
            return res;
        }
    }


    public ModificarHuespedResultDTO modificarHuesped(ModificarHuespedRequestDTO requestDTO) {
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

    public BajaHuespedResultDTO bajaHuesped(BajaHuespedRequestDTO requestDTO) {
        try {
            return this.service.eliminar(requestDTO);
        } catch (Exception e){
            this.logger.error("Error en HuespedController - bajaHuesped: " + e.getMessage(), e);
            return null;
        }
    }
}
