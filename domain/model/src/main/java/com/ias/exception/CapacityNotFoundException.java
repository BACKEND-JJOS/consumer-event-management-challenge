package com.ias.exception;

public class CapacityNotFoundException extends RuntimeException {
    public CapacityNotFoundException(String message) {
        super(message);
    }
}
