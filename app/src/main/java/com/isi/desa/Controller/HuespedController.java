package com.isi.desa.Controller;
import com.isi.desa.Dto.Huesped.*;
import com.isi.desa.Service.Implementations.HuespedService;
import com.isi.desa.Service.Implementations.Logger;
import com.isi.desa.Service.Interfaces.IHuespedService;
import com.isi.desa.Service.Interfaces.ILogger;

//@Controller //Descomentar para correr con Spring Boot
public class HuespedController {
    //@Autowired //Descomentar para correr con Spring Boot
    private final IHuespedService service;
    //@Autowired //Descomentar para correr con Spring Boot
    private final ILogger logger;

    // Constructor privado para singleton y para evitar instanciacion directa
    private HuespedController() {
        // Usar singletons de implementacion (se asume que existen getInstance())
        this.service = HuespedService.getInstance();
        this.logger = Logger.getInstance();
    }

    // Instancia unica (eager singleton)
    private static final HuespedController INSTANCE = new HuespedController();

    // Metodo publico para obtener la instancia
    public static HuespedController getInstance() {
        return INSTANCE;
    }

    public BuscarHuespedResultDTO buscarHuesped(BuscarHuespedRequestDTO requestDTO) {
        try{
            return this.service.buscarHuesped(requestDTO);
        } catch(Exception e){
            this.logger.error("Error en HuespedController - buscarHuesped: " + e.getMessage(), e);
            return null;
        }
    }

    public AltaHuespedResultDTO altaHuesped(AltaHuesperRequestDTO requestDTO) {
        AltaHuespedResultDTO res = new AltaHuespedResultDTO();

        try {
            // Ejecuta CU-09
            HuespedDTO creado = this.service.crear(requestDTO.huesped);

            // Alta exitosa
            res.resultado.id = 0;
            res.resultado.mensaje = "Huesped dado de alta exitosamente.";
            res.huesped = creado;

        } catch (IllegalArgumentException e) {
            // Error de datos / validación
            res.resultado.id = 2;
            res.resultado.mensaje = e.getMessage();

        } catch (Exception e) {
            // Error interno
            this.logger.error("Error en altaHuesped: " + e.getMessage(), e);
            res.resultado.id = 1;
            res.resultado.mensaje = "Ocurrio un error interno al realizar el alta del huesped.";
        }

        return res;
    }


    public ModificarHuespedResultDTO modificarHuesped(ModificarHuespedRequestDTO requestDTO) {
        try {
            return this.service.modificar(requestDTO);
        } catch (Exception e) {
            this.logger.error("Error en HuespedController - modificarHuesped: " + e.getMessage(), e);
            ModificarHuespedResultDTO res = new ModificarHuespedResultDTO();
            res.resultado = new com.isi.desa.Dto.Resultado();
            res.resultado.id = 1;
            res.resultado.mensaje = "Error interno";
            return res;
        }
    }

    public BajaHuespedResultDTO bajaHuesped(BajaHuespedRequestDTO requestDTO) {
        try {
            BajaHuespedResultDTO res = this.service.eliminar(requestDTO);
            return res;
        } catch (Exception e){
            this.logger.error("Error en HuespedController - bajaHuesped: " + e.getMessage(), e);
            return null;
        }
    }
}
