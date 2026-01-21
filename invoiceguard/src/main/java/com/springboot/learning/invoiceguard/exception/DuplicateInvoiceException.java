package com.springboot.learning.invoiceguard.exception;

public class DuplicateInvoiceException extends RuntimeException {
    public DuplicateInvoiceException(String message) {
        super(message);
    }
}
