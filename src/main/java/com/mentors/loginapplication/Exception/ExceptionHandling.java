package com.mentors.loginapplication.Exception;

import com.mentors.loginapplication.Model.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;

@RestControllerAdvice
public class ExceptionHandling {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String httpStatusMessage) {
        return ResponseEntity
                .status(HttpStatus.resolve(httpStatus.value()))
                .body(HttpResponse.builder()
                        .httpStatus(httpStatus)
                        .httpStatusCode(httpStatus.value())
                        .httpTimestamp(new Date(System.currentTimeMillis()))
                        .httpStatusMessage(httpStatusMessage)
                        .httpDeveloperMessage("Oops")
                        .httpStatusReason("test")
                        .build()
                );
    }

    @ExceptionHandler(EmailExistsException.class)
    public ResponseEntity<HttpResponse> emailExistException(EmailExistsException exception){
        LOGGER.info(exception.getMessage() + " Returning response back to client: " + HttpStatus.BAD_REQUEST);
        return createHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

}
