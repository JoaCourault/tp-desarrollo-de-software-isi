package com.isi.desa.Service.Implementations;

import com.isi.desa.Dao.Implementations.EstadiaDAO;
import com.isi.desa.Dao.Repositories.*;
import com.isi.desa.Dto.Estadia.EstadiaDTO;
import com.isi.desa.Dto.Factura.*;
import com.isi.desa.Dto.ResponsableDePago.ResponsableDePagoDTO;
import com.isi.desa.Dto.Resultado;
import com.isi.desa.Dto.Servicio.ServicioDTO;
import com.isi.desa.Model.Entities.Estadia.Estadia;
import com.isi.desa.Model.Entities.Factura.Factura;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Model.Entities.NotaDeCredito.NotaDeCredito;
import com.isi.desa.Model.Entities.ResponsableDePago.ResponsableDePago;
import com.isi.desa.Model.Entities.Servicio.Servicio;
import com.isi.desa.Service.Implementations.Validators.HabitacionValidator;
import com.isi.desa.Service.Interfaces.IFaucturacionService;
import com.isi.desa.Utils.Mappers.FacturacionMapper;
import com.isi.desa.Utils.Mappers.ResponsableDePagoMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;


@Service
public class FacturacionService implements IFaucturacionService {
    @Autowired
    HabitacionRepository habitacionRepository;
    @Autowired
    HabitacionValidator habitacionValidator;
    @Autowired
    EstadiaDAO estadiaDAO;
    @Autowired
    ResponsableDePagoRepository responsableDePagoRepository;
    @Autowired
    ServicioRepository servicioRepository;
    @Autowired
    EstadiaRepository estadiaRepository;
    @Autowired
    FacturaRepository facturaRepository;

    /*
        Resultados posibles:
        0  → OK
        1  → error
        >1 → inconsistencia
    * */
    @Override
    @Transactional(readOnly = true)
    public ObtenerResponsablesDePagoParaFacturacionResult obtenerResponsablesDePagoParaFacturacion(
            ObtenerResponsablesDePagoParaFacturacionRequest request) {
        ObtenerResponsablesDePagoParaFacturacionResult result = new ObtenerResponsablesDePagoParaFacturacionResult();
        result.resultado = validacionGeneralParaFacturacion(request.idHabitacion, request.momentoDeFecturacion);
        if (result.resultado.id != 0) return result;
        List<Estadia> estadiasDeLaHabitacion =
                estadiaDAO.findByIdHabitacionAndMoment(
                        request.idHabitacion,
                        request.momentoDeFecturacion
                );

        if (estadiasDeLaHabitacion.isEmpty()) {
            result.resultado.id = 1;
            result.resultado.mensaje = "No hay estadías activas para la habitación en el momento de salida indicado";
            return result;
        }

        if (estadiasDeLaHabitacion.size() > 1) {
            result.resultado.id = 2;
            result.resultado.mensaje = "Inconsistencia: múltiples estadías activas para la habitación";
            return result;
        }

        Estadia estadia = estadiasDeLaHabitacion.getFirst();

        Set<ResponsableDePago> responsablesDePago = new HashSet<>();

        for (Huesped huesped : estadia.getHuespedesHospedados()) {
            if (huesped.getEdad() < 18) continue; // Un huesped menor no puede ser responsable de pago. Nos ahorramos la busqueda en la base de datos.
            ResponsableDePago responsable =
                    responsableDePagoRepository.findPersonaFisicaByIdHuesped(
                            huesped.getIdHuesped()
                    );

            if (responsable != null) {
                responsablesDePago.add(responsable);
            }
        }

        result.responsablesDePago = ResponsableDePagoMapper.entitiesToDtos(new ArrayList<>(responsablesDePago));
        result.resultado.mensaje = "Responsables de pago obtenidos correctamente";

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public GenerarFacturacionHabitacionResult generarFacturacionParaHabitacion(
            GenerarFacturacionHabitacionRequest request
    ) {

        GenerarFacturacionHabitacionResult result =
                new GenerarFacturacionHabitacionResult();

        result.resultado.id = 0;
        result.resultado.mensaje = "Facturación generada correctamente";

        // Validamos responsable de pago existe
        ResponsableDePago responsableDePago =
                responsableDePagoRepository
                        .findById(request.idResponsableDePago)
                        .orElse(null);

        if (responsableDePago == null) {
            result.resultado.id = 1;
            result.resultado.mensaje =
                    "El responsable de pago indicado no existe";
            return result;
        }

        // Validacion general de habitación + momento
        result.resultado = validacionGeneralParaFacturacion(
            request.idHabitacion,
            request.momentoDeFecturacion
        );

        if (result.resultado.id != 0) {
            return result;
        }

        // Obtener estadías facturables
        List<Estadia> estadias =
                estadiaRepository.findByHabitacionAndMoment(
                        request.idHabitacion,
                        request.momentoDeFecturacion
                );

        if (estadias.isEmpty()) {
            result.resultado.id = 2;
            result.resultado.mensaje =
                    "No existen estadías facturables para la habitación";
            return result;
        }

        // Validaciones de negocio
        for (Estadia estadia : estadias) {

            // 1 No facturada antes
            if (estadia.getFacturas() != null &&
                    !estadia.getFacturas().isEmpty()) {
                result.resultado.id = 3;
                result.resultado.mensaje =
                        "La estadía " + estadia.getIdEstadia() +
                                " ya se encuentra facturada";
                return result;
            }

            // 2 Responsable valido (si no es cobro a terceros)
            if (!request.cobroATerceros) {

                boolean responsableValido = false;
                int i = 0;

                List<Huesped> huespedes =
                        estadia.getHuespedesHospedados();

                while (!responsableValido && i < huespedes.size()) {
                    Huesped huesped = huespedes.get(i);

                    ResponsableDePago r =
                            responsableDePagoRepository
                                    .findPersonaFisicaByIdHuesped(
                                            huesped.getIdHuesped()
                                    );

                    if (r != null && r.getIdResponsableDePago().equals(request.idResponsableDePago)) {
                        responsableValido = true;
                    }

                    i++;
                }

                if (!responsableValido) {
                    result.resultado.id = 1;
                    result.resultado.mensaje =
                            "El responsable de pago no es válido para la estadía " + estadia.getIdEstadia();
                    return result;
                }
            }
        }

        // ARMAR FACTURA
        Factura factura = new Factura();
        factura.setNombre(
                "Factura habitación " + request.idHabitacion + " - Fecha y hora: " + request.momentoDeFecturacion
        );
        factura.setDetalle(
                "Factura simulada para la habitación " +
                        request.idHabitacion +
                        " con momento de salida " +
                        request.momentoDeFecturacion
        );
        factura.setResponsableDePago(responsableDePago);
        factura.setEstadias(new ArrayList<>(estadias));

        // obtenemos los servicios
        List<Servicio> servicios = new ArrayList<>();

        for (Estadia estadia : estadias) {
            servicios.addAll(servicioRepository.findByEstadia_IdEstadia(estadia.getIdEstadia()));
        }

        factura.setServicios(servicios);

        // calcular total
        BigDecimal total = BigDecimal.ZERO;

        for (Estadia estadia : estadias) {
            total = total.add(estadia.getValorTotalEstadia());
        }

        for (Servicio servicio : servicios) {
            total = total.add(servicio.getPrecio());
        }

        factura.setTotal(total);

        // devolvemos factura simulada
        result.resultado.id = 0;
        result.resultado.mensaje = "Facturación generada correctamente, aún no confirmada.";
        result.facturaGenerada = FacturacionMapper.factura_entityToDto(factura);

        return result;
    }


    @Override
    @Transactional
    public ConfirmarFacturacionResult confirmarFacturacion(
            ConfirmarFacturacionRequest request
    ) {

        ConfirmarFacturacionResult result = new ConfirmarFacturacionResult();

        // Valor por defecto OK
        result.resultado.id = 0;
        result.resultado.mensaje = "Factura confirmada correctamente";

        FacturaDTO dto = request.facturaDTO;
        if (dto == null) {
            result.resultado.id = 1;
            result.resultado.mensaje = "La factura a confirmar es nula";
            return result;
        }


        // 1. Responsable de pago
        ResponsableDePago responsableDePago =
                responsableDePagoRepository
                        .findById(dto.responsableDePago.idResponsableDePago)
                        .orElse(null);

        if (responsableDePago == null) {
            result.resultado.id = 1;
            result.resultado.mensaje = "El responsable de pago no existe";
            return result;
        }

        // 2. Estadias
        List<Estadia> estadias = new ArrayList<>();
        if (dto.estadias != null) {
            for (EstadiaDTO eDto : dto.estadias) {
                Estadia estadia = estadiaRepository
                        .findById(eDto.idEstadia)
                        .orElse(null);

                if (estadia == null) {
                    result.resultado.id = 1;
                    result.resultado.mensaje =
                            "La estadía " + eDto.idEstadia + " no existe";
                    return result;
                }

                estadias.add(estadia);
            }
        }

        // 3. Servicios
        List<Servicio> servicios = new ArrayList<>();
        if (dto.servicios != null) {
            for (ServicioDTO sDto : dto.servicios) {
                Servicio servicio =
                        servicioRepository.findById(sDto.id).orElse(null);

                if (servicio == null) {
                    result.resultado.id = 1;
                    result.resultado.mensaje =
                            "El servicio " + sDto.id + " no existe";
                    return result;
                }

                servicios.add(servicio);
            }
        }

        // Al menos una estadía o servicio
        if (estadias.isEmpty() && servicios.isEmpty()) {
            result.resultado.id = 1;
            result.resultado.mensaje = "La factura debe contener al menos una estadía o un servicio";
            return result;
        }

        // 4. Total
        BigDecimal total = BigDecimal.ZERO;

        for (Estadia estadia : estadias) {
            total = total.add(estadia.getValorTotalEstadia());
        }

        for (Servicio servicio : servicios) {
            total = total.add(servicio.getPrecio());
        }

        // 5. Construcción de la entidad
        Factura factura = new Factura();
        factura.setNombre(dto.nombre);
        factura.setDetalle(dto.detalle);
        factura.setTotal(dto.total);
        factura.setResponsableDePago(responsableDePago);
        factura.setEstadias(estadias);
        factura.setServicios(servicios);


        // 6. Persistir
        Factura facturaPersistida = facturaRepository.save(factura);

        result.facturaConfirmada =
                FacturacionMapper.factura_entityToDto(facturaPersistida);

        return result;
    }


    // ===============================================
    // Validaciones Para Facturacion
    // ===============================================
    private Resultado validacionGeneralParaFacturacion(String idHabitacion, LocalDateTime momentoDeFecturacion) {
        Resultado resultado = new Resultado();
        resultado.id = 0;
        resultado.mensaje = "Validacion correcta";

        if (momentoDeFecturacion == null) {
            resultado.id = 1;
            resultado.mensaje = "El momento de salida es obligatorio";
            return resultado;
        }

        Boolean habitacionExistente = habitacionValidator.validateExistById(idHabitacion);
        if (!habitacionExistente) {
            resultado.id = 1;
            resultado.mensaje = "La habitacion no existe";
            return resultado;
        }

        Boolean habitacionDisponible = habitacionValidator.validateExistById(idHabitacion);
        if (habitacionDisponible) {
            resultado.id = 1;
            resultado.mensaje = "La habitacion no esta ocupada";
            return resultado;
        }

        return resultado;
    }
}
