package com.springboot.learning.invoiceguard.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class InvoiceAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long invoiceId;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus oldStatus;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus newStatus;

    private LocalDateTime timeStamp;

    @Enumerated(EnumType.STRING)
    private InvoiceAction action;

    public InvoiceAudit()
    {

    }

    public InvoiceAudit(Long invoiceId, InvoiceStatus oldStatus, InvoiceStatus newStatus, LocalDateTime timeStamp, InvoiceAction action) {
        this.invoiceId = invoiceId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.timeStamp = timeStamp;
        this.action = action;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public InvoiceStatus getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(InvoiceStatus oldStatus) {
        this.oldStatus = oldStatus;
    }

    public InvoiceStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(InvoiceStatus newStatus) {
        this.newStatus = newStatus;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public InvoiceAction getAction() {
        return action;
    }

    public void setAction(InvoiceAction action) {
        this.action = action;
    }
}
