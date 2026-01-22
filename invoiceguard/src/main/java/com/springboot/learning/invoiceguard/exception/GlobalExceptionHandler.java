package com.springboot.learning.invoiceguard.exception;

import com.springboot.learning.invoiceguard.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler(value = InvoiceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleInvoiceNotFound(InvoiceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse("INVOICE_NOT_FOUND", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND); // Returns 404
    }

    @ExceptionHandler(VendorNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public  ResponseEntity<ErrorResponse> handleVendorNotFoundException(VendorNotFoundException ex) {
        ErrorResponse error = new ErrorResponse("VENDOR_NOT_FOUND", ex.getMessage());
        return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BlockedVendorException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public  ResponseEntity<ErrorResponse> handleBlockedVendorException(BlockedVendorException ex) {
        ErrorResponse error = new ErrorResponse("VENDOR_BLOCKED", ex.getMessage());
        return new ResponseEntity<>(error,HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DuplicateInvoiceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public  ResponseEntity<ErrorResponse> handleDuplicateInvoiceException(DuplicateInvoiceException ex) {
        ErrorResponse error = new ErrorResponse("DUPLICATE_INVOICE", ex.getMessage());
        return new ResponseEntity<>(error,HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvoiceStatusException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public  ResponseEntity<ErrorResponse> handleInvoiceStatusException(InvoiceStatusException ex) {
        ErrorResponse error = new ErrorResponse("INVALID_INVOICE_STATUS", ex.getMessage());
        return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
    }


}
