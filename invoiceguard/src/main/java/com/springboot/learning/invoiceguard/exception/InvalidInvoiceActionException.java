package com.springboot.learning.invoiceguard.exception;

public class InvalidInvoiceActionException extends RuntimeException {
    public InvalidInvoiceActionException(String message) {
        super(message);
    }
}
