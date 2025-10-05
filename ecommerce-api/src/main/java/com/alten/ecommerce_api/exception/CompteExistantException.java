package com.alten.ecommerce_api.exception;

public class CompteExistantException extends RuntimeException {
    public CompteExistantException(String message) {
        super(message);
    }
}
