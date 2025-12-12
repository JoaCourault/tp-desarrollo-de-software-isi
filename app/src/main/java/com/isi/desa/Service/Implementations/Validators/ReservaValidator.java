package com.isi.desa.Service.Implementations.Validators;

import com.isi.desa.Dao.Interfaces.IReservaDAO;
import com.isi.desa.Service.Interfaces.Validators.IReservaValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class ReservaValidator implements IReservaValidator {

    @Autowired
    private IReservaDAO reservaDAO;

    // Mismo Regex que en HuespedValidator
    private static final String TEXT_ONLY_REGEX = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$";
    private static final Pattern TEXT_PATTERN = Pattern.compile(TEXT_ONLY_REGEX);

    @Override
    public RuntimeException validateBuscar(String apellido, String nombre) {
        // 1. Apellido es obligatorio
        if (apellido == null || apellido.trim().isEmpty()) {
            return new IllegalArgumentException("El apellido es obligatorio para buscar.");
        }

        // 2. Validar caracteres Apellido
        if (!TEXT_PATTERN.matcher(apellido).matches()) {
            return new IllegalArgumentException("El apellido contiene caracteres inválidos.");
        }

        // 3. Validar Nombre (solo si no es nulo/vacío, ya que es opcional)
        if (nombre != null && !nombre.trim().isEmpty()) {
            if (!TEXT_PATTERN.matcher(nombre).matches()) {
                return new IllegalArgumentException("El nombre contiene caracteres inválidos.");
            }
        }

        return null; // Todo OK
    }

    @Override
    public RuntimeException validateEliminar(String idReserva) {
        if (idReserva == null || idReserva.trim().isEmpty()) {
            return new IllegalArgumentException("El ID de reserva es inválido.");
        }
        if (reservaDAO.getById(idReserva) == null) {
            return new RuntimeException("La reserva no existe."); // Podrías crear ReservaNotFoundException
        }
        return null;
    }
}