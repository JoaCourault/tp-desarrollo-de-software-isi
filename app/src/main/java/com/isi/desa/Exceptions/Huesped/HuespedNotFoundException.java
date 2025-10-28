package com.isi.desa.Exceptions.Huesped;

/**
 * Se lanza cuando se intenta acceder o buscar un huesped que no existe.
 */
public class HuespedNotFoundException extends RuntimeException {

    public HuespedNotFoundException(String message) {
        super(message);
    }
}
