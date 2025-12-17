package com.isi.desa.Service.Implementations.Validators;

import com.isi.desa.Dao.Interfaces.IHuespedDAO;
import com.isi.desa.Dao.Interfaces.ITipoDocumentoDAO;
import com.isi.desa.Dto.Huesped.HuespedDTO;
import com.isi.desa.Dto.TipoDocumento.TipoDocumentoDTO;
import com.isi.desa.Exceptions.Huesped.CannotCreateHuespedException;
import com.isi.desa.Exceptions.Huesped.CannotDeleteHuespedException;
import com.isi.desa.Model.Entities.Huesped.Huesped;
import com.isi.desa.Model.Entities.Tipodocumento.TipoDocumento;
import com.isi.desa.Model.Entities.Estadia.Estadia;
import com.isi.desa.Service.Implementations.Validators.DireccionValidator;
import com.isi.desa.Service.Implementations.Validators.HuespedValidator;
import com.isi.desa.Utils.Mappers.DireccionMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HuespedValidatorTest {

    @Mock
    private IHuespedDAO huespedDAO;

    @Mock
    private ITipoDocumentoDAO tipoDocumentoDAO;

    @Mock
    private DireccionMapper direccionMapper;

    @Mock
    private DireccionValidator direccionValidator;

    @InjectMocks
    private HuespedValidator validator;

    // ---------- TEST 1: HAPPY PATH  ----------
    @Test
    void validateCreate_datosValidos_ok() {
        HuespedDTO dto = dtoValido();

        when(tipoDocumentoDAO.obtener("DNI")).thenReturn(new TipoDocumento());


        CannotCreateHuespedException ex = validator.validateCreate(dto);

        assertNull(ex);
    }

    // ---------- TEST 2: MUCHOS ERRORES JUNTOS ----------
    @Test
    void validateCreate_multiplesErrores_devuelveError() {
        HuespedDTO dto = new HuespedDTO();
        dto.nombre = "123";
        dto.apellido = "";
        dto.numDoc = "";
        dto.tipoDoc = null;
        dto.fechaNac = LocalDate.now().plusDays(1);
        dto.posicionIva = "Responsable Inscripto";
        dto.cuit = "123";

        CannotCreateHuespedException ex = validator.validateCreate(dto);

        assertNotNull(ex);
    }

    // ---------- TEST 3: DELETE CON ESTADIAS ----------
    @Test
    void validateDelete_conEstadias_error() {
        Huesped huesped = new Huesped();
        huesped.setIdHuesped("1");

        when(huespedDAO.getById("1")).thenReturn(huesped);
        when(huespedDAO.obtenerEstadiasDeHuesped("1"))
                .thenReturn(List.of(new Estadia()));

        CannotDeleteHuespedException ex = validator.validateDelete("1");

        assertNotNull(ex);
    }

    // ---------- DTO BASE VALIDO ----------
    private HuespedDTO dtoValido() {
        HuespedDTO dto = new HuespedDTO();
        dto.nombre = "Juan";
        dto.apellido = "Perez";
        dto.numDoc = "12345678";
        dto.nacionalidad = "Argentina";
        dto.ocupacion = "Ingeniero";
        dto.fechaNac = LocalDate.of(1995, 1, 1);
        dto.posicionIva = "Consumidor Final";
        dto.cuit = null;

        TipoDocumentoDTO tipo = new TipoDocumentoDTO();
        tipo.tipoDocumento = "DNI";
        dto.tipoDoc = tipo;

        dto.direccion = null; // ya cubrís rama null

        return dto;
    }

    @Test
    void validateExists_idNulo_error() {
        RuntimeException ex = validator.validateExists(null);
        assertNotNull(ex);
    }

    @Test
    void validateDelete_sinEstadias_ok() {
        Huesped h = new Huesped();
        h.setIdHuesped("1");

        when(huespedDAO.getById("1")).thenReturn(h);
        when(huespedDAO.obtenerEstadiasDeHuesped("1")).thenReturn(List.of());

        CannotDeleteHuespedException ex = validator.validateDelete("1");

        assertNull(ex);
    }

    @Test
    void validateUpdate_dtoNulo_error() {
        var ex = validator.validateUpdate(null);
        assertNotNull(ex);
    }

    @Test
    void validateUpdate_cuitInvalido_error() {
        HuespedDTO dto = new HuespedDTO();
        dto.idHuesped = "1";
        dto.cuit = "123";

        TipoDocumentoDTO tipo = new TipoDocumentoDTO();
        tipo.tipoDocumento = "DNI";
        dto.tipoDoc = tipo;

        when(tipoDocumentoDAO.obtener("DNI")).thenReturn(new TipoDocumento());

        var ex = validator.validateUpdate(dto);

        assertNotNull(ex);
    }

    @Test
    void validateCreate_tipoDocumentoNull_error() {
        HuespedDTO dto = new HuespedDTO();
        dto.nombre = "Juan";
        dto.apellido = "Perez";
        dto.numDoc = "12345678";
        dto.fechaNac = LocalDate.of(1990, 1, 1);
        dto.tipoDoc = null;

        CannotCreateHuespedException ex = validator.validateCreate(dto);

        assertNotNull(ex);
        assertTrue(ex.getMessage().contains("tipo de documento"));
    }

    @Test
    void validateCreate_fechaNacimientoNull_error() {
        HuespedDTO dto = new HuespedDTO();
        dto.nombre = "Juan";
        dto.apellido = "Perez";
        dto.numDoc = "12345678";
        dto.fechaNac = null;

        TipoDocumentoDTO tipo = new TipoDocumentoDTO();
        tipo.tipoDocumento = "DNI";
        dto.tipoDoc = tipo;

        when(tipoDocumentoDAO.obtener("DNI")).thenReturn(new TipoDocumento());

        CannotCreateHuespedException ex = validator.validateCreate(dto);

        assertNotNull(ex);
        assertTrue(ex.getMessage().contains("fecha de nacimiento"));
    }

    @Test
    void create_datosValidos_creaHuesped() {
        HuespedDTO dto = dtoValido();

        when(tipoDocumentoDAO.obtener("DNI")).thenReturn(new TipoDocumento());


        Huesped h = validator.create(dto);

        assertNotNull(h);
        assertEquals("Juan", h.getNombre());
    }


}
