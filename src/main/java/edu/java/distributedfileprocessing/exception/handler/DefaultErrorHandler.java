package edu.java.distributedfileprocessing.exception.handler;

import edu.java.distributedfileprocessing.exception.NotFoundException;
import edu.java.distributedfileprocessing.exception.NotSupportedAuthenticationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class DefaultErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Void> handleNotFoundException(NotFoundException ex) {
        log.debug("Requested resource does not exists", ex);
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(NotSupportedAuthenticationException.class)
    public ResponseEntity<String> handleAuthenticationException(NotSupportedAuthenticationException ex) {
        log.warn("In use not supported authentication", ex);
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Unsupported authentication received");
    }

}
