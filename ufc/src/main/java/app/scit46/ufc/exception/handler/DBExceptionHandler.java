package app.scit46.ufc.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import app.scit46.ufc.exception.DBNotFoundException;

@RestControllerAdvice
public class DBExceptionHandler {
    @ExceptionHandler(DBNotFoundException.class)
    public ResponseEntity<String> handleDBNotFoundException(DBNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}
