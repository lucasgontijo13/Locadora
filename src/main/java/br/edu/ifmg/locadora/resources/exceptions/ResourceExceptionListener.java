package br.edu.ifmg.locadora.resources.exceptions;

import br.edu.ifmg.locadora.services.exceptions.DataBaseException;
import br.edu.ifmg.locadora.services.exceptions.ResourceNotFound;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
@ControllerAdvice
public class ResourceExceptionListener {

    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<StandartError> resorceNotFound(ResourceNotFound ex, HttpServletRequest request){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new StandartError(
                        Instant.now(),
                        HttpStatus.NOT_FOUND.value(),
                        "Resource not found",
                        request.getRequestURI(),
                        ex.getMessage()
                ));

    }
    @ExceptionHandler(DataBaseException.class)
    public ResponseEntity<StandartError> dataBaseExcepion(DataBaseException ex, HttpServletRequest request){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new StandartError(
                        Instant.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        "database exception",
                        request.getRequestURI(),
                        ex.getMessage()
                ));

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> methodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ValidationError error = new ValidationError();
        error.setTimestamp(Instant.now());
        error.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
        error.setError("Validation exception");
        error.setPath(request.getRequestURI());
        error.setMessage(ex.getMessage());
        for (FieldError f : ex.getFieldErrors()) {
            error.addFieldMessage(f.getField(),f.getDefaultMessage());
        }

        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(error);
    }


}