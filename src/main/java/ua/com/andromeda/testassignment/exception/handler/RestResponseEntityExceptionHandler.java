package ua.com.andromeda.testassignment.exception.handler;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ua.com.andromeda.testassignment.exception.ErrorResponse;
import ua.com.andromeda.testassignment.exception.InvalidRangeException;
import ua.com.andromeda.testassignment.exception.InvalidUUIDException;
import ua.com.andromeda.testassignment.exception.UserNotFoundException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String message = ex.getBindingResult().getFieldError().getDefaultMessage();
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), message, status.value());
        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({InvalidRangeException.class, InvalidUUIDException.class})
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        return handleConflict(ex, request, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request, HttpStatus status) {
        String errMessage = ex.getMessage();
        LocalDateTime timestamp = LocalDateTime.now();
        ErrorResponse errorResponse = new ErrorResponse(timestamp, errMessage, status.value());
        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(UserNotFoundException.class)
    protected ResponseEntity<Object> handleConflict(UserNotFoundException ex, WebRequest request) {
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
