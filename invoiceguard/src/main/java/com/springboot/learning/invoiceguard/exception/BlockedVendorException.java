package com.springboot.learning.invoiceguard.exception;

public class BlockedVendorException extends RuntimeException {
    public BlockedVendorException(String message) {
        super(message);
    }
}
