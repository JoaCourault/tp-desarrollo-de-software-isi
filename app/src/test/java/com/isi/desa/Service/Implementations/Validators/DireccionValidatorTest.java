package com.isi.desa.Service.Implementations.Validators;

import com.isi.desa.Dto.Direccion.DireccionDTO;
import com.isi.desa.Exceptions.Direccion.InvalidDirectionException;
import com.isi.desa.Service.Implementations.Validators.DireccionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DireccionValidatorTest {

    private DireccionValidator validator;

    @BeforeEach
    void setUp() {
        validator = new DireccionValidator();
    }

    @Test
    void direccionNull_devuelveError() {
        InvalidDirectionException ex = validator.validate(null);

        assertNotNull(ex);
        assertEquals("La direccion no puede ser nula", ex.getMessage());
    }

    @Test
    void direccionValida_noDevuelveError() {
        DireccionDTO dto = new DireccionDTO();
        dto.calle = "San Martin";
        dto.numero = "123";
        dto.codigoPostal = "3000";
        dto.pais = "Argentina";
        dto.provincia = "Santa Fe";
        dto.localidad = "Santa Fe";
        dto.piso = "2";

        InvalidDirectionException ex = validator.validate(dto);

        assertNull(ex);
    }

    @Test
    void codigoPostalConCaracteresInvalidos_devuelveError() {
        DireccionDTO dto = new DireccionDTO();
        dto.calle = "San Martin";
        dto.numero = "123";
        dto.codigoPostal = "30-00";
        dto.pais = "Argentina";
        dto.provincia = "Santa Fe";
        dto.localidad = "Santa Fe";

        InvalidDirectionException ex = validator.validate(dto);

        assertNotNull(ex);
        assertTrue(ex.getMessage().contains("codigo postal"));
    }

    @Test
    void pisoNoNumerico_devuelveError() {
        DireccionDTO dto = new DireccionDTO();
        dto.calle = "San Martin";
        dto.numero = "123";
        dto.codigoPostal = "3000";
        dto.pais = "Argentina";
        dto.provincia = "Santa Fe";
        dto.localidad = "Santa Fe";
        dto.piso = "PB";

        InvalidDirectionException ex = validator.validate(dto);

        assertNotNull(ex);
        assertTrue(ex.getMessage().contains("piso"));
    }



}
