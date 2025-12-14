package com.isi.desa.Service.Implementations;


import com.isi.desa.Dao.Interfaces.IDireccionDAO;
import com.isi.desa.Dao.Repositories.ResponsableDePagoRepository;
import com.isi.desa.Dto.Direccion.DireccionDTO;
import com.isi.desa.Dto.ResponsableDePago.*;
import com.isi.desa.Dto.ResponsableDePago.PersonaJuridica.PersonaJuridicaDTO;
import com.isi.desa.Model.Entities.Direccion.Direccion;
import com.isi.desa.Model.Entities.ResponsableDePago.ResponsableDePago;
import com.isi.desa.Service.Interfaces.IResponsableDePagoService;
import com.isi.desa.Utils.Mappers.ResponsableDePagoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ResponsableDePagoService implements IResponsableDePagoService {
    @Autowired
    ResponsableDePagoRepository responsableDePagoRepository;
    @Autowired
    private IDireccionDAO direccionDAO;

    @Override
    public List<String> obtenerRazonesSocialesResponsablesDePago() {
        return responsableDePagoRepository.findAllRazonesSociales();
    }

    @Override
    public BuscarResponsableDePagoResult BuscarResponsableDePago(BuscarResponsableDePagoRequest request) {
        BuscarResponsableDePagoResult result = new BuscarResponsableDePagoResult();
        result.resultado.id = 0;
        result.resultado.mensaje = "Responsables de pago encontrados exitosamente.";
        result.responsableDePagos = new java.util.ArrayList<>();

        Set<ResponsableDePago> responsablesSet = new HashSet<>();
        boolean cuitVacio = request.cuit == null || request.cuit.trim().isEmpty();
        boolean razonVacia = request.razonSocial == null || request.razonSocial.trim().isEmpty();

        if (cuitVacio && razonVacia) {
            responsablesSet.addAll(responsableDePagoRepository.findAll());
        } else {
            if (!cuitVacio) {
                responsablesSet.addAll(responsableDePagoRepository.findPersonaJuridicaByCuit(request.cuit));
                responsablesSet.addAll(responsableDePagoRepository.findPersonaFisicaByCuit(request.cuit));
            }
            if (!razonVacia) {
                responsablesSet.addAll(responsableDePagoRepository.findByRazonSocialContainingIgnoreCase(request.razonSocial));
            }
        }
        result.responsableDePagos.addAll(ResponsableDePagoMapper.entitiesToDtos(new java.util.ArrayList<>(responsablesSet)));

        if (result.responsableDePagos.isEmpty()) {
            result.resultado.id = 1;
            result.resultado.mensaje = "No se encontraron responsables de pago con los criterios proporcionados.";
        }
        return result;
    }

    @Override
    public AltaResponsableDePagoResult AltaResponsableDePago(AltaResponsableDePagoRequest request) {
        AltaResponsableDePagoResult result = new AltaResponsableDePagoResult();
        try {
            if (request.responsableDePagoDTO instanceof PersonaJuridicaDTO pjDto) {
                List<ResponsableDePago> cuitList = responsableDePagoRepository.findPersonaJuridicaByCuit(pjDto.cuit);
                cuitList.addAll(responsableDePagoRepository.findPersonaFisicaByCuit(pjDto.cuit));
                boolean existeCuit = !cuitList.isEmpty();
                boolean existeRazon = !responsableDePagoRepository.findByRazonSocialContainingIgnoreCase(pjDto.razonSocial).isEmpty();
                boolean existeDireccion = pjDto.direccion != null && pjDto.direccion.idDireccion != null &&
                    !responsableDePagoRepository.findPersonaJuridicaByDireccion(pjDto.direccion.idDireccion).isEmpty();
                if (existeCuit || existeRazon || existeDireccion) {
                    result.resultado.id = 1;
                    if (existeDireccion) {
                        result.resultado.mensaje = "Ya existe un responsable de pago con la misma dirección.";
                    } else {
                        result.resultado.mensaje = "Ya existe un responsable de pago con el mismo CUIT o razón social.";
                    }
                    return result;
                }
            }
            request.responsableDePagoDTO.idResponsableDePago = null; // Asegurar que el ID sea nulo para una nueva entidad
            ResponsableDePago nuevoResponsableDePago = ResponsableDePagoMapper.dtoToEntity(request.responsableDePagoDTO);
            ResponsableDePago responsableDePagoGuardado = responsableDePagoRepository.save(nuevoResponsableDePago);
            result.responsableDePagoGenerado = ResponsableDePagoMapper.entityToDto(responsableDePagoGuardado);
            result.resultado.id = 0;
            result.resultado.mensaje = "Responsable de pago creado exitosamente.";
        } catch (Exception e) {
            result.resultado.id = 2;
            result.resultado.mensaje = "Error al crear responsable de pago: " + e.getMessage();
        }
        return result;
    }

    @Override
    public ModificarResponsableDePagoResult ModificarResponsableDePago(ModificarResponsableDePagoRequest request) {
        ModificarResponsableDePagoResult result = new ModificarResponsableDePagoResult();
        try {
            String idActual = request.responsableDePagoAModificar.idResponsableDePago;
            if (!responsableDePagoRepository.existsById(idActual)) {
                result.resultado.id = 1;
                result.resultado.mensaje = "El responsable de pago no existe.";
                return result;
            }
            if (request.responsableDePagoAModificar instanceof PersonaJuridicaDTO pjDto) {
                List<ResponsableDePago> razonList = responsableDePagoRepository.findByRazonSocialContainingIgnoreCase(pjDto.razonSocial);
                boolean existeRazon = razonList.stream()
                        .anyMatch(r -> !r.getIdResponsableDePago().equals(idActual) &&
                                r instanceof com.isi.desa.Model.Entities.ResponsableDePago.PersonaJuridica &&
                                ((com.isi.desa.Model.Entities.ResponsableDePago.PersonaJuridica) r).getRazonSocial().equalsIgnoreCase(pjDto.razonSocial));
                if (existeRazon) {
                    result.resultado.id = 2;
                    result.resultado.mensaje = "Ya existe otro responsable de pago con la misma razón social.";
                    return result;
                }
                boolean existeCuit = responsableDePagoRepository.findPersonaJuridicaByCuit(pjDto.cuit).stream()
                        .anyMatch(r -> !r.getIdResponsableDePago().equals(idActual))
                    || responsableDePagoRepository.findPersonaFisicaByCuit(pjDto.cuit).stream()
                        .anyMatch(r -> !r.getIdResponsableDePago().equals(idActual));
                if (existeCuit) {
                    result.resultado.id = 2;
                    result.resultado.mensaje = "Ya existe otro responsable de pago con el mismo CUIT.";
                    return result;
                }
                boolean existeDireccion = pjDto.direccion != null && pjDto.direccion.idDireccion != null &&
                    responsableDePagoRepository.findPersonaJuridicaByDireccion(pjDto.direccion.idDireccion).stream()
                        .anyMatch(r -> !r.getIdResponsableDePago().equals(idActual));
                if (existeDireccion) {
                    result.resultado.id = 2;
                    result.resultado.mensaje = "Ya existe otro responsable de pago con la misma dirección.";
                    return result;
                }
                // --- Lógica de dirección ---
                if (pjDto.direccion != null) {
                    DireccionDTO nuevaDir = pjDto.direccion;
                    Direccion direccionFinal = null;
                    // Buscar si ya existe una dirección con los mismos datos relevantes
                    List<Direccion> direccionesIguales = direccionDAO.obtenerTodas();
                    direccionFinal = direccionesIguales.stream().filter(dir ->
                        dir.getCalle().equalsIgnoreCase(nuevaDir.calle)
                        && dir.getNumero().equalsIgnoreCase(nuevaDir.numero)
                        && ((dir.getPiso() == null && nuevaDir.piso == null) || (dir.getPiso() != null && dir.getPiso().equals(nuevaDir.piso)))
                        && ((dir.getDepartamento() == null && nuevaDir.departamento == null) || (dir.getDepartamento() != null && dir.getDepartamento().equalsIgnoreCase(nuevaDir.departamento)))
                        && dir.getCp().equalsIgnoreCase(nuevaDir.cp)
                        && dir.getLocalidad().equalsIgnoreCase(nuevaDir.localidad)
                        && dir.getProvincia().equalsIgnoreCase(nuevaDir.provincia)
                        && dir.getPais().equalsIgnoreCase(nuevaDir.pais)
                    ).findFirst().orElse(null);
                    if (direccionFinal != null) {
                        // Si ya existe una dirección igual, la reutilizamos
                        pjDto.direccion = com.isi.desa.Utils.Mappers.DireccionMapper.entityToDto(direccionFinal);
                    } else {
                        // Si no existe, crearla
                        direccionFinal = direccionDAO.crear(nuevaDir);
                        pjDto.direccion = com.isi.desa.Utils.Mappers.DireccionMapper.entityToDto(direccionFinal);
                    }
                }
            }
            else if (request.responsableDePagoAModificar instanceof com.isi.desa.Dto.ResponsableDePago.PersonaFisica.PersonaFisicaDTO pfDto) {
                boolean existeCuit = responsableDePagoRepository.findPersonaFisicaByCuit(pfDto.huesped.cuit).stream()
                        .anyMatch(r -> !r.getIdResponsableDePago().equals(idActual))
                    || responsableDePagoRepository.findPersonaJuridicaByCuit(pfDto.huesped.cuit).stream()
                        .anyMatch(r -> !r.getIdResponsableDePago().equals(idActual));
                if (existeCuit) {
                    result.resultado.id = 2;
                    result.resultado.mensaje = "Ya existe otro responsable de pago con el mismo CUIT.";
                    return result;
                }
            }
            ResponsableDePago responsableDePagoExistente = ResponsableDePagoMapper.dtoToEntity(request.responsableDePagoAModificar);
            ResponsableDePago responsableDePagoActualizado = responsableDePagoRepository.save(responsableDePagoExistente);
            result.responsableDePagoModificado = ResponsableDePagoMapper.entityToDto(responsableDePagoActualizado);
            result.resultado.id = 0;
            result.resultado.mensaje = "Responsable de pago modificado exitosamente.";
        } catch (Exception e) {
            result.resultado.id = 3;
            result.resultado.mensaje = "Error al modificar responsable de pago: " + e.getMessage();
        }
        return result;
    }
}
