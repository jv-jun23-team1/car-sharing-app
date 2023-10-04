package com.team01.carsharingapp.exception;

import java.time.LocalDateTime;
import java.util.List;
import lombok.NonNull;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {
        ExceptionBody body = new ExceptionBody(LocalDateTime.now(),
                HttpStatus.BAD_REQUEST,
                ex.getBindingResult().getAllErrors().stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .toList());
        return new ResponseEntity<>(body, headers, status);
    }

    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<Object> handleRegistrationException(
            RegistrationException ex) {
        ExceptionBody body = new ExceptionBody(LocalDateTime.now(),
                HttpStatus.BAD_REQUEST,
                List.of(ex.getMessage()));
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RentalException.class)
    public ResponseEntity<Object> handleRentalException(
            RentalException ex) {
        ExceptionBody body = new ExceptionBody(LocalDateTime.now(),
                HttpStatus.BAD_REQUEST,
                List.of(ex.getMessage()));
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(
            AccessDeniedException ex) {
        ExceptionBody body = new ExceptionBody(LocalDateTime.now(),
                HttpStatus.FORBIDDEN,
                List.of(ex.getMessage()));
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(
            EntityNotFoundException ex) {
        ExceptionBody body = new ExceptionBody(LocalDateTime.now(),
                HttpStatus.NOT_FOUND,
                List.of(ex.getMessage()));
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleOtherExceptions(Exception ex) {
        ExceptionBody body = new ExceptionBody(LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                List.of(ex.getMessage()));
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
