package com.example.NeoBank.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {


        super(message);
    }
}
