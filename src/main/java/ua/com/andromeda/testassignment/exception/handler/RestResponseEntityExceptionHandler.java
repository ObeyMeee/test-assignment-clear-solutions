package ua.com.andromeda.testassignment.exception.handler;

import jakarta.validation.ConstraintViolationException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ua.com.andromeda.testassignment.exception.ErrorResponse;
import ua.com.andromeda.testassignment.exception.IllegalAgeException;
import ua.com.andromeda.testassignment.exception.InvalidRangeException;
import ua.com.andromeda.testassignment.exception.InvalidUUIDException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ConstraintViolationException.class})
    protected ResponseEntity<Object> handleConflict(ConstraintViolationException ex, WebRequest request) {
        String errMessage = ex.getConstraintViolations()
                .stream()
                .findAny()
                .get()
                .getMessage();
        int status = HttpStatus.BAD_REQUEST.value();
        LocalDateTime timestamp = LocalDateTime.now();
        ErrorResponse errorResponse = new ErrorResponse(timestamp, errMessage, status);
        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({IllegalAgeException.class, InvalidRangeException.class, InvalidUUIDException.class})
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        return handleConflict(ex, request, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request, HttpStatus status) {
        String errMessage = ex.getMessage();
        LocalDateTime timestamp = LocalDateTime.now();
        ErrorResponse errorResponse = new ErrorResponse(timestamp, errMessage, status.value());
        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<Object> handleConflict(ResourceNotFoundException ex, WebRequest request) {
        return handleConflict(ex, request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleConflict(Exception ex, WebRequest request) {
        ex.printStackTrace();
        String errMessage = "Something went wrong";
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        LocalDateTime timestamp = LocalDateTime.now();
        ErrorResponse errorResponse = new ErrorResponse(timestamp, errMessage, status.value());
        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), status, request);
    }
}
