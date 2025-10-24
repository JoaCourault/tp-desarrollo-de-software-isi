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
        throw new UnsupportedOperationException("Not supported yet."); //Se implementa para SCRUM-10
    }
    public ModificarHuespedResultDTO modificarHuesped(ModificarHuespedRequestDTO requestDTO) {
        throw new UnsupportedOperationException("Not supported yet."); //Se implementa para SCRUM-11
    }
    public BajaHuespedResultDTO bajaHuesped(BajaHuespedRequestDTO requestDTO) {
        throw new UnsupportedOperationException("Not supported yet."); //Se implementa para SCRUM-12
    }
}
