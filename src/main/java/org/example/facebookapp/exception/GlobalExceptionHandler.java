package org.example.facebookapp.exception;

import org.example.facebookapp.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    /*@ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(),
        "Zasób nie znaleziony", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }*/

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFoundException(ResourceNotFoundException ex, ModelMap model) {
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(),
        "Zasób nie znaleziony", ex.getMessage());
        model.addAttribute("errorResponse", errorResponse);
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGlobalException(Exception ex, ModelMap model) {
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
        "Wystąpił Błąd", ex.getMessage());
        model.addAttribute("errorResponse", errorResponse);
        return "error";
    }
}
