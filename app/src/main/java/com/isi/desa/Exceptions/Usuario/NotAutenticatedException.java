package com.isi.desa.Exceptions.Usuario;

/**
 * Se lanza cuando un usuario no esta autenticado y se intenta acceder a un recurso protegido.
 */
public class NotAutenticatedException extends RuntimeException {

    public NotAutenticatedException(String message) {
        super(message);
    }
}
