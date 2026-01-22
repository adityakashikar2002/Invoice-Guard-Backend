package com.springboot.learning.invoiceguard.dto;

import com.springboot.learning.invoiceguard.model.InvoiceStatus;

public class InvoiceActionResponse {

    private String message;
    private Long id;
    private InvoiceStatus newStatus;

    public InvoiceActionResponse() {

    }

    public InvoiceActionResponse(String message, Long id, InvoiceStatus newStatus) {
        this.message = message;
        this.id = id;
        this.newStatus = newStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InvoiceStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(InvoiceStatus newStatus) {
        this.newStatus = newStatus;
    }
}
