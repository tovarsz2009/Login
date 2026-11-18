package com.thebestadso.login.exception;

/**
 * Se lanza cuando el usuario o la clave no son correctos
 */
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Credenciales inválidas");
    }
}
