package com.isi.desa.Service.Implementations;

import com.isi.desa.Dao.Implementations.EstadiaDAO;
import com.isi.desa.Dao.Repositories.*;
import com.isi.desa.Dto.Estadia.EstadiaDTO;
import com.isi.desa.Dto.Factura.*;
import com.isi.desa.Dto.Resultado;
import com.isi.desa.Dto.Servicio.ServicioDTO;
import com.isi.desa.Model.Entities.Estadia.Estadia;
import com.isi.desa.Model.Entities.Factura.Factura;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Model.Entities.ResponsableDePago.ResponsableDePago;
import com.isi.desa.Model.Entities.ResponsableDePago.PersonaFisica;
import com.isi.desa.Model.Entities.Servicio.Servicio;
import com.isi.desa.Service.Implementations.Validators.HabitacionValidator;
import com.isi.desa.Service.Interfaces.IFaucturacionService;
import com.isi.desa.Utils.Mappers.FacturacionMapper;
import com.isi.desa.Utils.Mappers.ResponsableDePagoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.isi.desa.Dto.Factura.GenerarFacturaRequestDTO;
import com.isi.desa.Dto.Factura.GenerarFacturaResultDTO;
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
    @Autowired
    private HuespedRepository huespedRepository;

    // Inyección de Mappers
    @Autowired
    private FacturacionMapper facturacionMapper;

    @Autowired
    private ResponsableDePagoMapper responsableDePagoMapper;

    @Override
    @Transactional(readOnly = true)
    public ObtenerResponsablesDePagoParaFacturacionResult obtenerResponsablesDePagoParaFacturacion(
            ObtenerResponsablesDePagoParaFacturacionRequest request) {

        ObtenerResponsablesDePagoParaFacturacionResult result = new ObtenerResponsablesDePagoParaFacturacionResult();
        result.resultado = validacionGeneralParaFacturacion(request.idHabitacion, request.momentoDeFecturacion);
        if (result.resultado.id != 0) return result;

        List<Estadia> estadiasDeLaHabitacion = estadiaDAO.findByIdHabitacionAndMoment(
                request.idHabitacion,
                request.momentoDeFecturacion
        );

        if (estadiasDeLaHabitacion.isEmpty()) {
            result.resultado.id = 1;
            result.resultado.mensaje = "No hay estadías activas para la habitación en el momento indicado";
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
            if (huesped.getEdad() < 18) continue;
            ResponsableDePago responsable = responsableDePagoRepository.findPersonaFisicaByIdHuesped(huesped.getIdHuesped());

            if (responsable != null) {
                responsablesDePago.add(responsable);
            }
        }

        // Uso de instancia inyectada
        result.responsablesDePago = responsableDePagoMapper.entitiesToDtos(new ArrayList<>(responsablesDePago));
        result.resultado.mensaje = "Responsables de pago obtenidos correctamente";

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public GenerarFacturacionHabitacionResult generarFacturacionParaHabitacion(
            GenerarFacturacionHabitacionRequest request
    ) {
        GenerarFacturacionHabitacionResult result = new GenerarFacturacionHabitacionResult();
        result.resultado.id = 0;
        result.resultado.mensaje = "Facturación generada correctamente";

        ResponsableDePago responsableDePago = responsableDePagoRepository
                .findById(request.idResponsableDePago)
                .orElse(null);

        if (responsableDePago == null) {
            result.resultado.id = 1;
            result.resultado.mensaje = "El responsable de pago indicado no existe";
            return result;
        }

        result.resultado = validacionGeneralParaFacturacion(request.idHabitacion, request.momentoDeFecturacion);
        if (result.resultado.id != 0) {
            return result;
        }

        List<Estadia> estadias = estadiaRepository.findByHabitacionAndMoment(
                request.idHabitacion,
                request.momentoDeFecturacion
        );

        if (estadias.isEmpty()) {
            result.resultado.id = 2;
            result.resultado.mensaje = "No existen estadías facturables para la habitación";
            return result;
        }

        for (Estadia estadia : estadias) {
            if (estadia.getFacturas() != null && !estadia.getFacturas().isEmpty()) {
                result.resultado.id = 3;
                result.resultado.mensaje = "La estadía " + estadia.getIdEstadia() + " ya se encuentra facturada";
                return result;
            }

            if (!request.cobroATerceros) {
                boolean responsableValido = false;
                int i = 0;
                List<Huesped> huespedes = estadia.getHuespedesHospedados();

                while (!responsableValido && i < huespedes.size()) {
                    Huesped huesped = huespedes.get(i);
                    ResponsableDePago r = responsableDePagoRepository.findPersonaFisicaByIdHuesped(huesped.getIdHuesped());

                    if (r != null && r.getIdResponsableDePago().equals(request.idResponsableDePago)) {
                        responsableValido = true;
                    }
                    i++;
                }

                if (!responsableValido) {
                    result.resultado.id = 1;
                    result.resultado.mensaje = "El responsable de pago no es válido para la estadía " + estadia.getIdEstadia();
                    return result;
                }
            }
        }

        Factura factura = new Factura();
        factura.setNombre("Factura habitación " + request.idHabitacion + " - Fecha: " + request.momentoDeFecturacion);
        factura.setDetalle("Factura simulada para hab " + request.idHabitacion);
        factura.setResponsableDePago(responsableDePago);
        factura.setEstadias(new ArrayList<>(estadias));

        List<Servicio> servicios = new ArrayList<>();
        for (Estadia estadia : estadias) {
            servicios.addAll(servicioRepository.findByEstadia_IdEstadia(estadia.getIdEstadia()));
        }
        factura.setServicios(servicios);

        BigDecimal total = BigDecimal.ZERO;
        for (Estadia estadia : estadias) {
            total = total.add(estadia.getValorTotalEstadia());
        }
        for (Servicio servicio : servicios) {
            total = total.add(servicio.getPrecio());
        }
        factura.setTotal(total);

        // Uso de instancia inyectada
        result.facturaGenerada = facturacionMapper.factura_entityToDto(factura);

        return result;
    }

    @Override
    @Transactional
    public ConfirmarFacturacionResult confirmarFacturacion(ConfirmarFacturacionRequest request) {
        ConfirmarFacturacionResult result = new ConfirmarFacturacionResult();
        result.resultado.id = 0;
        result.resultado.mensaje = "Factura confirmada correctamente";

        FacturaDTO dto = request.facturaDTO;
        if (dto == null) {
            result.resultado.id = 1;
            result.resultado.mensaje = "La factura a confirmar es nula";
            return result;
        }

        ResponsableDePago responsableDePago = responsableDePagoRepository
                .findById(dto.responsableDePago.idResponsableDePago)
                .orElse(null);

        if (responsableDePago == null) {
            result.resultado.id = 1;
            result.resultado.mensaje = "El responsable de pago no existe";
            return result;
        }

        List<Estadia> estadias = new ArrayList<>();
        if (dto.estadias != null) {
            for (EstadiaDTO eDto : dto.estadias) {
                Estadia estadia = estadiaRepository.findById(eDto.idEstadia).orElse(null);
                if (estadia == null) {
                    result.resultado.id = 1;
                    result.resultado.mensaje = "La estadía " + eDto.idEstadia + " no existe";
                    return result;
                }
                estadias.add(estadia);
            }
        }

        List<Servicio> servicios = new ArrayList<>();
        if (dto.servicios != null) {
            for (ServicioDTO sDto : dto.servicios) {
                Servicio servicio = servicioRepository.findById(sDto.id).orElse(null);
                if (servicio == null) {
                    result.resultado.id = 1;
                    result.resultado.mensaje = "El servicio " + sDto.id + " no existe";
                    return result;
                }
                servicios.add(servicio);
            }
        }

        if (estadias.isEmpty() && servicios.isEmpty()) {
            result.resultado.id = 1;
            result.resultado.mensaje = "La factura debe contener al menos una estadía o un servicio";
            return result;
        }

        // Re-calculo de total para seguridad
        BigDecimal total = BigDecimal.ZERO;
        for (Estadia estadia : estadias) total = total.add(estadia.getValorTotalEstadia());
        for (Servicio servicio : servicios) total = total.add(servicio.getPrecio());

        Factura factura = new Factura();
        factura.setNombre(dto.nombre);
        factura.setDetalle(dto.detalle);
        factura.setTotal(dto.total); // O usar 'total' recalculado
        factura.setResponsableDePago(responsableDePago);
        factura.setEstadias(estadias);
        factura.setServicios(servicios);

        Factura facturaPersistida = facturaRepository.save(factura);

        // Uso de instancia inyectada
        result.facturaConfirmada = facturacionMapper.factura_entityToDto(facturaPersistida);

        return result;
    }

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
    @Transactional
    public GenerarFacturaResultDTO generarFacturaYCheckOut(GenerarFacturaRequestDTO request) {

        // Buscar Estadía
        Estadia estadia = estadiaRepository.findById(request.idEstadia)
                .orElseThrow(() -> new IllegalArgumentException("La estadía no existe."));

        // BUSQUEDA DE RESPONSABLE
        ResponsableDePago responsable = null;

        // Intentar por ID directo (si ya era responsable)
        Optional<ResponsableDePago> opResponsable = responsableDePagoRepository.findById(request.idResponsable);
        if (opResponsable.isPresent()) {
            responsable = opResponsable.get();
        } else {
            // Intentar buscar si existe como Persona Física vinculada a ese ID de Huésped
            responsable = responsableDePagoRepository.findPersonaFisicaByIdHuesped(request.idResponsable);
        }

        // Si no existe como responsable, pero es un Huésped, lo creamos ahora mismo.
        if (responsable == null) {
            Optional<Huesped> opHuesped = huespedRepository.findById(request.idResponsable);
            if (opHuesped.isPresent()) {
                Huesped huesped = opHuesped.get();

                // Creamos la entidad PersonaFisica que envuelve al Huesped
                PersonaFisica nuevoResponsable = new PersonaFisica();
                nuevoResponsable.setHuesped(huesped);

                // Guardamos para generar el ID de Responsable
                responsable = responsableDePagoRepository.save(nuevoResponsable);
            } else {
                throw new IllegalArgumentException("No se encontró el responsable de pago ni el huésped con ID: " + request.idResponsable);
            }
        }

        // Crear Entidad Factura
        Factura factura = new Factura();
        factura.setFecha(LocalDateTime.now());
        factura.setTipo(request.tipoFactura);
        factura.setTotal(request.total);
        factura.setResponsableDePago(responsable);

        List<Estadia> estadias = new ArrayList<>();
        estadias.add(estadia);
        factura.setEstadias(estadias);

        factura.setDetalle("Check-out Habitación " + (estadia.getHabitaciones().isEmpty() ? "?" : estadia.getHabitaciones().get(0).getNumero()));
        factura.setNombre("Factura " + request.tipoFactura + " - CheckOut");

        // Guardar Factura
        factura = facturaRepository.save(factura);

        // CHECK-OUT (Liberar Habitación)
        estadia.setCheckOut(LocalDateTime.now());
        estadiaRepository.save(estadia);

        // Retornar número
        // Usamos un hash positivo simple para simular un número secuencial
        String nroComprobante = "0001-" + String.format("%08d", factura.getIdFactura() != null ? (factura.getIdFactura().hashCode() & 0x7FFFFFFF) : System.currentTimeMillis());

        return new GenerarFacturaResultDTO(nroComprobante);
    }
}
