package com.epam.esm.gift.controller;

import com.epam.esm.gift.model.ErrorResponse;
import com.epam.esm.gift.model.exception.ResourceNotFoundException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ErrorController {
    private final MessageSource messageSource;

    @Autowired
    public ErrorController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @GetMapping("/errors")
    public ResponseEntity<ErrorResponse> renderErrorPage(HttpServletRequest httpRequest) {
        String errorMsg = "";
        int httpErrorCode = getErrorCode(httpRequest);
        switch (httpErrorCode) {
            case 400: {
                errorMsg = "message.error400";
                break;
            }
            case 401: {
                errorMsg = "message.error401";
                break;
            }
            case 404: {
                errorMsg = "message.error404";
                break;
            }
            case 500: {
                errorMsg = "message.error500";
                break;
            }
        }
        String errorMessage = messageSource.getMessage(errorMsg, new Object[]{}, LocaleContextHolder.getLocale());
        ErrorResponse errorResponse = new ErrorResponse(errorMessage, httpErrorCode);

        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatusCode.valueOf(httpErrorCode));
    }

    private int getErrorCode(HttpServletRequest httpRequest) {
        return (Integer) httpRequest
                .getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
    }
}