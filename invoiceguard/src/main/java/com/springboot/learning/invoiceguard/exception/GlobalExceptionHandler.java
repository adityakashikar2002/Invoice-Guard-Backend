package com.springboot.learning.invoiceguard.exception;

import com.springboot.learning.invoiceguard.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler(value = InvoiceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponseDTO> handleInvoiceNotFound(InvoiceNotFoundException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO("INVOICE_NOT_FOUND", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND); // Returns 404
    }

    @ExceptionHandler(VendorNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public  ResponseEntity<ErrorResponseDTO> handleVendorNotFoundException(VendorNotFoundException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO("VENDOR_NOT_FOUND", ex.getMessage());
        return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BlockedVendorException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public  ResponseEntity<ErrorResponseDTO> handleBlockedVendorException(BlockedVendorException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO("VENDOR_BLOCKED", ex.getMessage());
        return new ResponseEntity<>(error,HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DuplicateInvoiceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public  ResponseEntity<ErrorResponseDTO> handleDuplicateInvoiceException(DuplicateInvoiceException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO("DUPLICATE_INVOICE", ex.getMessage());
        return new ResponseEntity<>(error,HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvoiceStatusException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public  ResponseEntity<ErrorResponseDTO> handleInvoiceStatusException(InvoiceStatusException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO("INVALID_INVOICE_STATUS", ex.getMessage());
        return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
    }


}
