package com.springboot.learning.invoiceguard.exception;

public class InvoiceStatusException extends RuntimeException {
    public InvoiceStatusException(String message) {
        super(message);
    }
}
