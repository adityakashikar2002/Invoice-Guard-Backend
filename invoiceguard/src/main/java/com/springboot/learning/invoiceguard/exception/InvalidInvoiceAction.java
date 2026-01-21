package com.springboot.learning.invoiceguard.exception;

public class InvalidInvoiceAction extends RuntimeException {
    public InvalidInvoiceAction(String message) {
        super(message);
    }
}
