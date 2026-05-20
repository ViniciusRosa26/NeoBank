package com.example.NeoBank.handler;


import com.example.NeoBank.exception.BadRequestException;
import com.example.NeoBank.exception.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler  {

    @ExceptionHandler(BadRequestException.class)

    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException e) {

        ErrorResponse errorResponse = new ErrorResponse(
                e.getMessage(),
                String.valueOf(HttpStatus.BAD_REQUEST.value())
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);

    }

}
