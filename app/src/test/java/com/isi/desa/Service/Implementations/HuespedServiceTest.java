package com.isi.desa.Service.Implementations;

import com.isi.desa.Dao.Implementations.DireccionDAO;
import com.isi.desa.Dao.Interfaces.IHuespedDAO;
import com.isi.desa.Dao.Repositories.EstadiaRepository;
import com.isi.desa.Dao.Repositories.HuespedRepository;
import com.isi.desa.Dto.Direccion.DireccionDTO;
import com.isi.desa.Dto.Huesped.*;
import com.isi.desa.Dto.TipoDocumento.TipoDocumentoDTO;
import com.isi.desa.Exceptions.Huesped.CannotCreateHuespedException;
import com.isi.desa.Exceptions.Huesped.CannotDeleteHuespedException;
import com.isi.desa.Exceptions.Huesped.CannotModifyHuespedEsception;
import com.isi.desa.Exceptions.Huesped.HuespedDuplicadoException;
import com.isi.desa.Model.Entities.Direccion.Direccion;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Service.Interfaces.Validators.IHuespedValidator;
import com.isi.desa.Utils.Mappers.HuespedMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HuespedServiceTest {

    @InjectMocks
    private HuespedService service;

    @Mock private IHuespedValidator validator;
    @Mock private IHuespedDAO dao;
    @Mock private DireccionDAO direccionDAO;
    @Mock private HuespedMapper huespedMapper;
    @Mock private HuespedRepository repository;
    @Mock private EstadiaRepository estadiaRepository;

    /* =====================
       CREAR
       ===================== */

    @Test
    void crear_validatorError_lanzaException() {
        HuespedDTO dto = new HuespedDTO();
        when(validator.validateCreate(dto))
                .thenReturn(new CannotCreateHuespedException("error"));

        assertThrows(
                CannotCreateHuespedException.class,
                () -> service.crear(dto, false)
        );
    }

    @Test
    void crear_duplicado_noAceptar_lanzaException() {
        HuespedDTO dto = new HuespedDTO();
        dto.numDoc = "123";
        dto.tipoDoc = new TipoDocumentoDTO();
        dto.tipoDoc.tipoDocumento = "DNI";

        when(validator.validateCreate(dto)).thenReturn(null);
        when(repository.existsByNumDocAndTipoDoc_TipoDocumento("123", "DNI"))
                .thenReturn(true);

        assertThrows(
                HuespedDuplicadoException.class,
                () -> service.crear(dto, false)
        );
    }

    /* =====================
       ELIMINAR
       ===================== */

    @Test
    void eliminar_validatorError() {
        BajaHuespedRequestDTO req = new BajaHuespedRequestDTO();
        req.idHuesped = "H1";

        when(validator.validateDelete("H1"))
                .thenReturn((CannotDeleteHuespedException) new RuntimeException("error"));

        BajaHuespedResultDTO res = service.eliminar(req);

        assertEquals(2, res.resultado.id);
    }

    @Test
    void eliminar_huespedNoExiste() {
        BajaHuespedRequestDTO req = new BajaHuespedRequestDTO();
        req.idHuesped = "H1";

        when(validator.validateDelete("H1")).thenReturn(null);
        when(repository.findById("H1")).thenReturn(Optional.empty());

        BajaHuespedResultDTO res = service.eliminar(req);

        assertEquals(1, res.resultado.id);
    }

    @Test
    void eliminar_conEstadias_noPermite() {
        BajaHuespedRequestDTO req = new BajaHuespedRequestDTO();
        req.idHuesped = "H1";

        Huesped h = new Huesped();
        when(validator.validateDelete("H1")).thenReturn(null);
        when(repository.findById("H1")).thenReturn(Optional.of(h));
        when(estadiaRepository.existsByHuespedesHospedados_IdHuesped("H1"))
                .thenReturn(true);

        BajaHuespedResultDTO res = service.eliminar(req);

        assertEquals(2, res.resultado.id);
    }

    /* =====================
       BUSCAR
       ===================== */

    @Test
    void buscar_sinFiltros() {
        when(dao.leerHuespedes()).thenReturn(List.of());

        BuscarHuespedResultDTO res = service.buscarHuesped(null);

        assertEquals(0, res.resultado.id);
        verify(dao).leerHuespedes();
    }

    /* =====================
       MODIFICAR
       ===================== */

    @Test
    void modificar_requestNull() {
        ModificarHuespedResultDTO res = service.modificar(null);

        assertEquals(2, res.resultado.id);
    }

    @Test
    void modificar_validatorError() {
        ModificarHuespedRequestDTO req = new ModificarHuespedRequestDTO();
        req.huesped = new HuespedDTO();
        req.huesped.idHuesped = "H1";

        when(validator.validateUpdate(req.huesped))
                .thenReturn((CannotModifyHuespedEsception) new RuntimeException("error"));

        ModificarHuespedResultDTO res = service.modificar(req);

        assertEquals(2, res.resultado.id);
    }

    @Test
    void crear_duplicado_aceptarIgualmente_true() throws Exception {
        HuespedDTO dto = new HuespedDTO();
        dto.numDoc = "123";
        dto.tipoDoc = new TipoDocumentoDTO();
        dto.tipoDoc.tipoDocumento = "DNI";

        when(validator.validateCreate(dto)).thenReturn(null);
        when(dao.crear(dto)).thenReturn(new Huesped());
        when(huespedMapper.entityToDTO(any())).thenReturn(new HuespedDTO());

        HuespedDTO res = service.crear(dto, true);

        assertNotNull(res);
    }

    @Test
    void crear_conDireccion_laPersiste() throws Exception {
        HuespedDTO dto = new HuespedDTO();
        dto.numDoc = "123";
        dto.tipoDoc = new TipoDocumentoDTO();
        dto.tipoDoc.tipoDocumento = "DNI";
        dto.direccion = new DireccionDTO();

        Direccion dir = new Direccion();
        dir.setIdDireccion("D1");

        when(validator.validateCreate(dto)).thenReturn(null);
        when(direccionDAO.crear(dto.direccion)).thenReturn(dir);
        when(dao.crear(dto)).thenReturn(new Huesped());
        when(huespedMapper.entityToDTO(any())).thenReturn(new HuespedDTO());

        HuespedDTO res = service.crear(dto, true);

        assertEquals("D1", dto.direccion.id);
    }

    @Test
    void eliminar_exitoso() {
        BajaHuespedRequestDTO req = new BajaHuespedRequestDTO();
        req.idHuesped = "H1";

        Huesped h = new Huesped();

        when(validator.validateDelete("H1")).thenReturn(null);
        when(repository.findById("H1")).thenReturn(Optional.of(h));
        when(estadiaRepository.existsByHuespedesHospedados_IdHuesped("H1"))
                .thenReturn(false);
        when(estadiaRepository.existsByTitularId("H1"))
                .thenReturn(false);
        when(repository.save(any())).thenReturn(h);
        when(huespedMapper.entityToDTO(any())).thenReturn(new HuespedDTO());

        BajaHuespedResultDTO res = service.eliminar(req);

        assertEquals(0, res.resultado.id);
    }

    @Test
    void buscar_conFiltros() {
        BuscarHuespedRequestDTO req = new BuscarHuespedRequestDTO();
        req.huesped = new HuespedDTO();
        req.huesped.nombre = "Juan";

        when(dao.buscarHuesped(any())).thenReturn(List.of());

        BuscarHuespedResultDTO res = service.buscarHuesped(req);

        assertEquals(0, res.resultado.id);
        verify(dao).buscarHuesped(any());
    }

    @Test
    void modificar_direccionInvalida() {
        ModificarHuespedRequestDTO req = new ModificarHuespedRequestDTO();
        req.huesped = new HuespedDTO();
        req.huesped.idHuesped = "H1";
        req.huesped.direccion = new DireccionDTO(); // sin id

        when(validator.validateUpdate(req.huesped)).thenReturn(null);

        ModificarHuespedResultDTO res = service.modificar(req);

        assertEquals(2, res.resultado.id);
    }

    @Test
    void crear_conDireccionPeroNoSeGuarda() throws Exception {
        HuespedDTO dto = new HuespedDTO();
        dto.numDoc = "123";
        dto.tipoDoc = new TipoDocumentoDTO();
        dto.tipoDoc.tipoDocumento = "DNI";
        dto.direccion = new DireccionDTO();

        when(validator.validateCreate(dto)).thenReturn(null);
        when(direccionDAO.crear(dto.direccion)).thenReturn(null);
        when(dao.crear(dto)).thenReturn(new Huesped());
        when(huespedMapper.entityToDTO(any())).thenReturn(new HuespedDTO());

        HuespedDTO res = service.crear(dto, true);

        assertNotNull(res);
    }

    @Test
    void eliminar_guardadoCorrecto() {
        BajaHuespedRequestDTO req = new BajaHuespedRequestDTO();
        req.idHuesped = "H1";

        Huesped h = new Huesped();

        when(validator.validateDelete("H1")).thenReturn(null);
        when(repository.findById("H1")).thenReturn(Optional.of(h));
        when(estadiaRepository.existsByHuespedesHospedados_IdHuesped("H1"))
                .thenReturn(false);
        when(estadiaRepository.existsByTitularId("H1"))
                .thenReturn(false);
        when(repository.save(any())).thenReturn(h);
        when(huespedMapper.entityToDTO(any())).thenReturn(new HuespedDTO());

        BajaHuespedResultDTO res = service.eliminar(req);

        assertEquals(0, res.resultado.id);
    }

    @Test
    void buscar_conFiltrosPeroDaoNull() {
        BuscarHuespedRequestDTO req = new BuscarHuespedRequestDTO();
        req.huesped = new HuespedDTO();
        req.huesped.apellido = "Perez";

        when(dao.buscarHuesped(any())).thenReturn(null);

        BuscarHuespedResultDTO res = service.buscarHuesped(req);

        assertEquals(0, res.resultado.id);
        assertTrue(res.huespedesEncontrados.isEmpty());
    }

    @Test
    void modificar_huespedNoEncontrado() {
        ModificarHuespedRequestDTO req = new ModificarHuespedRequestDTO();
        req.huesped = new HuespedDTO();
        req.huesped.idHuesped = "H1";
        req.huesped.direccion = new DireccionDTO();
        req.huesped.direccion.id = "D1";

        when(validator.validateUpdate(req.huesped)).thenReturn(null);
        when(repository.existsByNumDocAndTipoDoc_TipoDocumentoAndIdHuespedNot(
                any(), any(), any()))
                .thenReturn(false);
        when(dao.modificar(req.huesped)).thenReturn(null);

        ModificarHuespedResultDTO res = service.modificar(req);

        assertEquals(1, res.resultado.id);
    }


}
