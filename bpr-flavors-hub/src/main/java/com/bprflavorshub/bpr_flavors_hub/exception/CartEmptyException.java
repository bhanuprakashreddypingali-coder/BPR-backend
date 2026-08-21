package com.bprflavorshub.bpr_flavors_hub.exception;

public class CartEmptyException extends RuntimeException {

    public CartEmptyException(String message) {
        super(message);
    }
}