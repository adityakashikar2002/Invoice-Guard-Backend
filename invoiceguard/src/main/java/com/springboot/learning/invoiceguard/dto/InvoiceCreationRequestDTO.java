package com.springboot.learning.invoiceguard.dto;

import jakarta.validation.constraints.*;
import org.jspecify.annotations.NonNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InvoiceCreationRequestDTO {

    @NotBlank(message = "Every Invoice Needs Invoice No.")
    private String invoiceNo;

    @NotNull(message = "Invoice can't be created without Vendor")
    private Long vendorId;

    @NotBlank(message = "Client must be named on Invoice")
    private String billTo;

    @NotNull
    @Min(1)
    private BigDecimal amount;

    @NotNull
    @FutureOrPresent(message = "Invoice date can't be in past")
    private LocalDate invoiceDate;

    @Future
    private LocalDate dueDate;

    public InvoiceCreationRequestDTO() {

    }

    public InvoiceCreationRequestDTO(String invoiceNo, Long vendorId, String billTo, BigDecimal amount, LocalDate invoiceDate, LocalDate dueDate) {
        this.invoiceNo = invoiceNo;
        this.vendorId = vendorId;
        this.billTo = billTo;
        this.amount = amount;
        this.invoiceDate = invoiceDate;
        this.dueDate = dueDate;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public String getBillTo() {
        return billTo;
    }

    public void setBillTo(String billTo) {
        this.billTo = billTo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }
}
