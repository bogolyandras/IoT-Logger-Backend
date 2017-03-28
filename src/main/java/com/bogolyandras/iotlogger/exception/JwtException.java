package com.bogolyandras.iotlogger.exception;

public class JwtException extends Exception {

    public JwtException(String message) {
        super(message);
    }

    public JwtException(String message, Throwable cause) {
        super(message, cause);
    }

}
