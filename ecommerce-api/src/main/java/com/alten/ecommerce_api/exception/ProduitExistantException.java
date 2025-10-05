package com.alten.ecommerce_api.exception;

public class ProduitExistantException extends RuntimeException {
    public ProduitExistantException(String message) {
        super(message);
    }
}
