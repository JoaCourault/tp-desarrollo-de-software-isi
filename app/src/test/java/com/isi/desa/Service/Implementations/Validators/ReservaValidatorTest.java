package com.isi.desa.Service.Implementations.Validators;

import com.isi.desa.Dao.Interfaces.IReservaDAO;
import com.isi.desa.Model.Entities.Reserva.Reserva;
import com.isi.desa.Service.Implementations.Validators.ReservaValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaValidatorTest {

    @Mock
    private IReservaDAO reservaDAO;

    @InjectMocks
    private ReservaValidator validator;

    /* =======================
       Tests validateBuscar
       ======================= */

    @Test
    void validateBuscar_apellidoNull_devuelveError() {
        RuntimeException ex = validator.validateBuscar(null, "Juan");
        assertNotNull(ex);
        assertTrue(ex.getMessage().contains("apellido"));
    }

    @Test
    void validateBuscar_apellidoVacio_devuelveError() {
        RuntimeException ex = validator.validateBuscar("   ", "Juan");
        assertNotNull(ex);
    }

    @Test
    void validateBuscar_apellidoConCaracteresInvalidos_devuelveError() {
        RuntimeException ex = validator.validateBuscar("P3r3z!", "Juan");
        assertNotNull(ex);
    }

    @Test
    void validateBuscar_nombreInvalido_devuelveError() {
        RuntimeException ex = validator.validateBuscar("Perez", "J0an!");
        assertNotNull(ex);
    }

    @Test
    void validateBuscar_datosValidos_ok() {
        RuntimeException ex = validator.validateBuscar("Perez", "Juan");
        assertNull(ex);
    }

    /* =======================
       Tests validateEliminar
       ======================= */

    @Test
    void validateEliminar_idNull_devuelveError() {
        RuntimeException ex = validator.validateEliminar(null);
        assertNotNull(ex);
    }

    @Test
    void validateEliminar_idVacio_devuelveError() {
        RuntimeException ex = validator.validateEliminar("   ");
        assertNotNull(ex);
    }

    @Test
    void validateEliminar_reservaNoExiste_devuelveError() {
        when(reservaDAO.getById("123")).thenReturn(null);

        RuntimeException ex = validator.validateEliminar("123");

        assertNotNull(ex);
        assertTrue(ex.getMessage().contains("no existe"));
    }

    @Test
    void validateEliminar_reservaExiste_ok() {
        when(reservaDAO.getById("123")).thenReturn(mock(Reserva.class));

        RuntimeException ex = validator.validateEliminar("123");

        assertNull(ex);
    }
}
