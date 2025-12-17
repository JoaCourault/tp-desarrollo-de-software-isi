package com.isi.desa.Service.Implementations;

import com.isi.desa.Dao.Implementations.EstadiaDAO;
import com.isi.desa.Dao.Repositories.*;
import com.isi.desa.Dto.Factura.*;
import com.isi.desa.Dto.ResponsableDePago.PersonaFisica.PersonaFisicaDTO;
import com.isi.desa.Dto.ResponsableDePago.ResponsableDePagoDTO;
import com.isi.desa.Dto.Resultado;
import com.isi.desa.Model.Entities.Estadia.Estadia;
import com.isi.desa.Model.Entities.Factura.Factura;
import com.isi.desa.Model.Entities.Habitacion.Habitacion;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Model.Entities.ResponsableDePago.ResponsableDePago;
import com.isi.desa.Model.Entities.Servicio.Servicio;
import com.isi.desa.Service.Implementations.Validators.HabitacionValidator;
import com.isi.desa.Utils.Mappers.FacturacionMapper;
import com.isi.desa.Utils.Mappers.ResponsableDePagoMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FacturacionServiceTest {

    @InjectMocks
    private FacturacionService service;

    @Mock private HabitacionValidator habitacionValidator;
    @Mock private EstadiaDAO estadiaDAO;
    @Mock private HabitacionRepository habitacionRepository;
    @Mock private HuespedRepository huespedRepository;
    @Mock private ReservaRepository reservaRepository;
    @Mock private ResponsableDePagoRepository responsableDePagoRepository;
    @Mock private ServicioRepository servicioRepository;
    @Mock private EstadiaRepository estadiaRepository;
    @Mock private FacturaRepository facturaRepository;
    @Mock private FacturacionMapper facturacionMapper;
    @Mock private ResponsableDePagoMapper responsableDePagoMapper;

    /* ===========================
       obtenerResponsablesDePago
       =========================== */

    @Test
    void obtenerResponsables_idHabitacionInvalida_error() {
        ObtenerResponsablesDePagoParaFacturacionRequest req = new ObtenerResponsablesDePagoParaFacturacionRequest();
        req.idHabitacion = "H1";
        req.momentoDeFecturacion = LocalDateTime.now();

        when(habitacionValidator.validateExistById("H1")).thenReturn(false);

        ObtenerResponsablesDePagoParaFacturacionResult res =
                service.obtenerResponsablesDePagoParaFacturacion(req);

        assertNotNull(res);
        assertEquals(1, res.resultado.id);
    }

    @Test
    void obtenerResponsables_sinEstadias_error() {
        ObtenerResponsablesDePagoParaFacturacionRequest req = new ObtenerResponsablesDePagoParaFacturacionRequest();
        req.idHabitacion = "H1";
        req.momentoDeFecturacion = LocalDateTime.now();

        when(habitacionValidator.validateExistById("H1")).thenReturn(true);
        when(estadiaDAO.findByIdHabitacionAndMoment(any(), any()))
                .thenReturn(Collections.emptyList());

        ObtenerResponsablesDePagoParaFacturacionResult res =
                service.obtenerResponsablesDePagoParaFacturacion(req);

        assertEquals(1, res.resultado.id);
    }

    /* ===========================
       generarFacturacion
       =========================== */

    @Test
    void generarFacturacion_responsableInexistente_error() {
        GenerarFacturacionHabitacionRequest req = new GenerarFacturacionHabitacionRequest();
        req.idResponsableDePago = "RP1";
        req.idHabitacion = "H1";
        req.momentoDeFecturacion = LocalDateTime.now();

        when(responsableDePagoRepository.findById("RP1"))
                .thenReturn(Optional.empty());

        GenerarFacturacionHabitacionResult res =
                service.generarFacturacionParaHabitacion(req);

        assertEquals(1, res.resultado.id);
    }

    @Test
    void generarFacturacion_sinEstadias_error() {
        GenerarFacturacionHabitacionRequest req = new GenerarFacturacionHabitacionRequest();
        req.idResponsableDePago = "RP1";
        req.idHabitacion = "H1";
        req.momentoDeFecturacion = LocalDateTime.now();

        ResponsableDePago responsable = mock(ResponsableDePago.class);
        when(responsable.getIdResponsableDePago()).thenReturn("RP1");

        when(responsableDePagoRepository.findById("RP1"))
                .thenReturn(Optional.of(responsable));

        when(habitacionValidator.validateExistById("H1")).thenReturn(true);
        when(estadiaRepository.findByHabitacionAndMoment(any(), any()))
                .thenReturn(Collections.emptyList());

        GenerarFacturacionHabitacionResult res =
                service.generarFacturacionParaHabitacion(req);

        assertEquals(2, res.resultado.id);
    }

    /* ===========================
       confirmarFacturacion
       =========================== */

    @Test
    void confirmarFacturacion_facturaNula_error() {
        ConfirmarFacturacionRequest req = new ConfirmarFacturacionRequest();
        req.facturaDTO = null;

        ConfirmarFacturacionResult res = service.confirmarFacturacion(req);

        assertEquals(1, res.resultado.id);
    }

    @Test
    void confirmarFacturacion_responsableNoExiste_error() {
        ConfirmarFacturacionRequest req = new ConfirmarFacturacionRequest();
        req.facturaDTO = new FacturaDTO();
        ResponsableDePagoDTO responsableMock = mock(ResponsableDePagoDTO.class);
        responsableMock.idResponsableDePago = "RP1";

        req.facturaDTO.responsableDePago = responsableMock;

        req.facturaDTO.responsableDePago.idResponsableDePago = "RP1";

        when(responsableDePagoRepository.findById("RP1"))
                .thenReturn(Optional.empty());

        ConfirmarFacturacionResult res = service.confirmarFacturacion(req);

        assertEquals(1, res.resultado.id);
    }

    @Test
    void confirmarFacturacion_facturaNula() {
        ConfirmarFacturacionRequest req = new ConfirmarFacturacionRequest();
        req.facturaDTO = null;

        ConfirmarFacturacionResult res = service.confirmarFacturacion(req);

        assertEquals(1, res.resultado.id);
    }

    @Test
    void confirmarFacturacion_responsableNoExiste() {
        ConfirmarFacturacionRequest req = new ConfirmarFacturacionRequest();
        req.facturaDTO = new FacturaDTO();

        PersonaFisicaDTO responsable = new PersonaFisicaDTO();
        responsable.idResponsableDePago = "RP1";
        req.facturaDTO.responsableDePago = responsable;

        when(responsableDePagoRepository.findById("RP1"))
                .thenReturn(Optional.empty());

        ConfirmarFacturacionResult res = service.confirmarFacturacion(req);

        assertEquals(1, res.resultado.id);
    }

    @Test
    void generarFacturacion_responsableInexistente() {
        GenerarFacturacionHabitacionRequest req = new GenerarFacturacionHabitacionRequest();
        req.idResponsableDePago = "RP1";

        when(responsableDePagoRepository.findById("RP1"))
                .thenReturn(Optional.empty());

        GenerarFacturacionHabitacionResult res =
                service.generarFacturacionParaHabitacion(req);

        assertEquals(1, res.resultado.id);
    }

    @Test
    void obtenerResponsables_momentoNulo() {
        ObtenerResponsablesDePagoParaFacturacionRequest req =
                new ObtenerResponsablesDePagoParaFacturacionRequest();
        req.idHabitacion = "H1";
        req.momentoDeFecturacion = null;

        ObtenerResponsablesDePagoParaFacturacionResult res =
                service.obtenerResponsablesDePagoParaFacturacion(req);

        assertEquals(1, res.resultado.id);
    }


}
