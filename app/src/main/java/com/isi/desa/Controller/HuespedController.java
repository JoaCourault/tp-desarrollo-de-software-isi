package com.isi.desa.Controller;
import com.isi.desa.Dto.Huesped.*;
import com.isi.desa.Service.Implementations.HuespedService;
import com.isi.desa.Service.Implementations.Logger;
import com.isi.desa.Service.Interfaces.IHuespedService;
import com.isi.desa.Service.Interfaces.ILogger;

//@Controller //Descomentar para correr con Spring Boot
public class HuespedController {
    //@Autowired //Descomentar para correr con Spring Boot
    private IHuespedService service;
    //@Autowired //Descomentar para correr con Spring Boot
    private ILogger logger;

    // Constructor para inyección de dependencias manual (sin Spring Boot, borrar cuando se use Spring)
    public HuespedController() {
        this.service = new HuespedService();
        this.logger = new Logger();
    }
    public BuscarHuespedResultDTO buscarHuesped(BuscarHuespedRequestDTO requestDTO) {
        try{
            BuscarHuespedResultDTO res = this.service.buscarHuesped(requestDTO);
            return res;
        } catch(Exception e){
            this.logger.error("Error en HuespedController - buscarHuesped: " + e.getMessage(), e);
            return null;
        }
    }
    // HuespedController.java
    public AltaHuespedResultDTO altaHuesped(AltaHuespedRequestDTO requestDTO) {
        AltaHuespedResultDTO res = new AltaHuespedResultDTO();
        res.resultado = new com.isi.desa.Dto.Resultado();
        try {
            if (requestDTO == null || requestDTO.huesped == null) {
                res.resultado.id = 1;
                res.resultado.mensaje = "Solicitud inválida: falta el huésped.";
                return res;
            }
            HuespedDTO creado = this.service.crear(requestDTO.huesped);
            res.resultado.id = 0;
            res.resultado.mensaje = "Huésped creado con éxito. DNI: " + creado.numDoc;
            return res;
        } catch (Exception e) {
            this.logger.error("Error en altaHuesped: " + e.getMessage(), e);
            res.resultado.id = 1;
            res.resultado.mensaje = "No se pudo crear el huésped: " + e.getMessage();
            return res;
        }
    }

    public ModificarHuespedResultDTO modificarHuesped(ModificarHuespedRequestDTO requestDTO) {
        throw new UnsupportedOperationException("Not supported yet."); //Se implementa para SCRUM-11
    }


    public BajaHuespedResultDTO bajaHuesped(BajaHuespedRequestDTO requestDTO) {
        throw new UnsupportedOperationException("Not supported yet."); //Se implementa para SCRUM-12
    }
}
