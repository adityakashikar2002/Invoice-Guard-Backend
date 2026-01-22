package com.springboot.learning.invoiceguard.dto;

import com.springboot.learning.invoiceguard.model.InvoiceAction;
import jakarta.validation.constraints.NotNull;

public class InvoiceActionRequest {

    @NotNull
    private InvoiceAction action;

    public  InvoiceActionRequest() {

    }

    public InvoiceActionRequest(InvoiceAction action) {
        this.action = action;
    }

    public InvoiceAction getAction() {
        return action;
    }

    public void setAction(InvoiceAction action) {
        this.action = action;
    }
}
