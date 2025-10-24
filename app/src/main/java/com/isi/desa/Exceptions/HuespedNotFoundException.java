package com.isi.desa.Exceptions;

/**
 * Se lanza cuando se intenta acceder o buscar un huésped que no existe.
 */
public class HuespedNotFoundException extends RuntimeException {

    public HuespedNotFoundException(String message) {
        super(message);
    }
}
