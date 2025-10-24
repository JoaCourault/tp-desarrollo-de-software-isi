package com.isi.desa.Exceptions;

/**
 * Se lanza cuando un usuario no está autenticado y se intenta acceder a un recurso protegido.
 */
public class NotAutenticatedException extends RuntimeException {

    public NotAutenticatedException(String message) {
        super(message);
    }
}
