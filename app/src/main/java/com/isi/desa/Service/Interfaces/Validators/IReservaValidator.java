package com.isi.desa.Service.Interfaces.Validators;

public interface IReservaValidator {
    // Valida los inputs de búsqueda
    RuntimeException validateBuscar(String apellido, String nombre);
    // Valida si se puede eliminar (existencia)
    RuntimeException validateEliminar(String idReserva);
}
