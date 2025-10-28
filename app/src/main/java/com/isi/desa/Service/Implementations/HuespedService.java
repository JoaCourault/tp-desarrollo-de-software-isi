package com.isi.desa.Service.Implementations;

import com.isi.desa.Dao.Implementations.DireccionDAO;
import com.isi.desa.Dao.Implementations.HuespedDAO;
import com.isi.desa.Dao.Interfaces.IHuespedDAO;
import com.isi.desa.Dto.Huesped.*;
import com.isi.desa.Dto.Resultado;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Service.Interfaces.IHuespedService;
import com.isi.desa.Service.Implementations.Validators.HuespedValidator;
import com.isi.desa.Service.Interfaces.Validators.IHuespedValidator;
import com.isi.desa.Utils.Mappers.HuespedMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//@Service //Descomentar para Spring Boot
public class HuespedService implements IHuespedService {
    //@Autowired //Descomentar para Spring Boot
    private final IHuespedValidator validator;
    //@Autowired //Descomentar para Spring Boot
    private final IHuespedDAO dao;

    // Instancia unica (eager singleton)
    private static final HuespedService INSTANCE = new HuespedService();

    // Constructor privado para inyeccion manual
    private HuespedService() {
        this.dao = new HuespedDAO();
        this.validator = HuespedValidator.getInstance();
    }

    // Metodo publico para obtener la instancia
    public static HuespedService getInstance() {
        return INSTANCE;
    }

    @Override
    public HuespedDTO crear(HuespedDTO huespedDTO) {

        // 1) Validación (si hay error → IllegalArgumentException)
        validator.create(huespedDTO);

        // 2) Persistencia de Dirección
        DireccionDAO direccionDAO = new DireccionDAO();
        try {
            // intenta obtener la dirección (si ya existe → OK)
            direccionDAO.obtener(huespedDTO.direccion);
        } catch (Exception e) {
            // si no existe → se crea
            direccionDAO.crear(huespedDTO.direccion);
        }

        // 3) Persistencia del Huésped
        Huesped creado = dao.crear(huespedDTO);

        // 4) Convertir a DTO para devolver
        return HuespedMapper.entityToDTO(creado);
    }


    @Override
    public HuespedDTO modificar(HuespedDTO huespedDTO) {
        try {
            validator.create(huespedDTO);
            Huesped modificado = dao.modificar(huespedDTO);
            return HuespedMapper.entityToDTO(modificado);
        } catch (Exception e) {
            throw new RuntimeException("Error al modificar huesped: " + e.getMessage(), e);
        }
    }

    @Override
    public BajaHuespedResultDTO eliminar(BajaHuespedRequestDTO requestDTO) {
        BajaHuespedResultDTO res = new BajaHuespedResultDTO();

        res.resultado.id = 0; // Exito
        res.resultado.mensaje = "Huesped eliminado exitosamente.";

        RuntimeException validation = this.validator.validateDelete(requestDTO.idHuesped);
        if (validation != null) {
            res.resultado.id = 2; // Error de validacion
            res.resultado.mensaje = validation.getMessage();
            return res;
        }

        Huesped eliminado = dao.eliminar(requestDTO.idHuesped);
        if (eliminado == null) {
            res.resultado.id = 1; // Error interno
            res.resultado.mensaje = "No se pudo eliminar el huesped.";
            return res;
        }

        res.huesped = HuespedMapper.entityToDTO(eliminado);
        return res;
    }

    @Override
    public BuscarHuespedResultDTO buscarHuesped(BuscarHuespedRequestDTO requestDTO) {
        BuscarHuespedResultDTO resultDTO = new BuscarHuespedResultDTO();
        resultDTO.resultado = new Resultado();
        resultDTO.huespedesEncontrados = new ArrayList<>();

        List<HuespedDTO> huespedesEncontrados;

        if (requestDTO == null || requestDTO.huesped == null) {
            // Si no hay filtros, devolver todos los huespedes NO eliminados
            huespedesEncontrados = dao.leerHuespedes().stream()
                    .filter(h -> h != null && !h.isEliminado())
                    .map(HuespedMapper::entityToDTO)
                    .collect(Collectors.toList());
        } else {
            HuespedDTO filtros = requestDTO.huesped;

            huespedesEncontrados = dao.leerHuespedes().stream()
                    .filter(h -> h != null && !h.isEliminado())
                    .filter(h -> (filtros.nombre == null || (h.getNombre() != null && h.getNombre().equalsIgnoreCase(filtros.nombre))) &&
                            (filtros.apellido == null || (h.getApellido() != null && h.getApellido().equalsIgnoreCase(filtros.apellido))) &&
                            (filtros.numDoc == null || (h.getNumDoc() != null && h.getNumDoc().equals(filtros.numDoc))))
                    .map(HuespedMapper::entityToDTO)
                    .collect(Collectors.toList());
        }

        resultDTO.huespedesEncontrados = huespedesEncontrados;

        if (huespedesEncontrados.isEmpty()) {
            resultDTO.resultado.id = 2; // NoEncontrado (404)
            resultDTO.resultado.mensaje = "No se encontraron huespedes con los filtros especificados.";
        } else {
            resultDTO.resultado.id = 0; // Exito (200)
            resultDTO.resultado.mensaje = "Busqueda exitosa.";
        }

        return resultDTO;
    }
}
