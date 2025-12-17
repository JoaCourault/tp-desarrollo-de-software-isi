package com.isi.desa.Service.Implementations.Validators;

import com.isi.desa.Dao.Repositories.HabitacionRepository;
import com.isi.desa.Model.Entities.Habitacion.DobleEstandar;
import com.isi.desa.Model.Entities.Habitacion.Habitacion;
import com.isi.desa.Model.Enums.EstadoHabitacion;
import com.isi.desa.Service.Implementations.Validators.HabitacionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HabitacionValidatorTest {

    @InjectMocks
    private HabitacionValidator validator;

    @Mock
    private HabitacionRepository habitacionRepository;

    @Test
    void validateExistById_habitacionExiste_devuelveTrue() {
        Habitacion h = new DobleEstandar();

        when(habitacionRepository.findById("1"))
                .thenReturn(Optional.of(h));

        Boolean result = validator.validateExistById("1");

        assertTrue(result);
    }

    @Test
    void validateExistById_habitacionNoExiste_devuelveFalse() {
        when(habitacionRepository.findById("1"))
                .thenReturn(Optional.empty());

        Boolean result = validator.validateExistById("1");

        assertFalse(result);
    }

    @Test
    void validateHabitacionDisponible_estadoDisponible_devuelveTrue() {
        Habitacion h = new DobleEstandar();
        h.setEstado(EstadoHabitacion.DISPONIBLE);

        when(habitacionRepository.findById("1"))
                .thenReturn(Optional.of(h));

        Boolean result = validator.validateHabitacionDisponibleById("1");

        assertTrue(result);
    }

    @Test
    void validateHabitacionDisponible_estadoNoDisponible_devuelveFalse() {
        Habitacion h = new DobleEstandar();
        h.setEstado(EstadoHabitacion.OCUPADA);

        when(habitacionRepository.findById("1"))
                .thenReturn(Optional.of(h));

        Boolean result = validator.validateHabitacionDisponibleById("1");

        assertFalse(result);
    }

    @Test
    void validateHabitacionDisponible_habitacionNoExiste_devuelveFalse() {
        when(habitacionRepository.findById("1"))
                .thenReturn(Optional.empty());

        Boolean result = validator.validateHabitacionDisponibleById("1");

        assertFalse(result);
    }
}
