package com.epam.esm.gift.controller;

import com.epam.esm.gift.model.ErrorResponse;
import com.epam.esm.gift.model.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Profile;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestControllerAdvice
/*@Profile("prod")*/
public class ErrorControllerAdvice{
    private final MessageSource messageSource;

    @Autowired
    public ErrorControllerAdvice(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    //@ResponseStatus(HttpStatus.NOT_FOUND)
/*    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<String> handleNotFound(Exception exception) {
        System.out.println("oklskfdaf");
        String errorMessage = "Requested resource not found";
        int errorCode = 40401;
        return ResponseEntity.ok("Error 404");
    }*/
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex) {
        return new ResponseEntity<String>("fail validation", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        String errorMessage = messageSource.getMessage("error.resourceNotFound", new Object[]{ex.getResourceId()}, LocaleContextHolder.getLocale());
        ErrorResponse errorResponse = new ErrorResponse( errorMessage,HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        Map<String, String> errorMessages = new HashMap<>();

        for (FieldError fieldError : fieldErrors) {
            errorMessages.put(fieldError.getField(),fieldError.getDefaultMessage());
        }

        ErrorResponse errorResponse = new ErrorResponse(errorMessages.toString(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception exception) {
        exception.printStackTrace();
        return new ResponseEntity<String>("something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
