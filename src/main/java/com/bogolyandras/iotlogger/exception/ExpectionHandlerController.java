package com.bogolyandras.iotlogger.exception;

import com.bogolyandras.iotlogger.dto.Error;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class ExpectionHandlerController {

    @ExceptionHandler({NoSuchElementException.class})
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public Error returnNotFound(NoSuchElementException e) {
        return new Error(e.getMessage());
    }

    @ExceptionHandler({AuthenticationException.class})
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public Error returnBadLogin(Exception e) {
        return new Error(e.getMessage());
    }

}
