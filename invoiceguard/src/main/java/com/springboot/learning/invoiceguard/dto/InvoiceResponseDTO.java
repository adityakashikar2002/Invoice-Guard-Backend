package com.springboot.learning.invoiceguard.dto;

import com.springboot.learning.invoiceguard.model.VendorStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InvoiceResponseDTO {

    private Long id;

    private String invoiceNo;

    private Long vendorId;

    private String vendorName;

    private VendorStatus vendorStatus;

    private String billTo;

    private BigDecimal amount;

    private LocalDate invoiceDate;

    private LocalDate dueDate;

    public InvoiceResponseDTO() {

    }

    public InvoiceResponseDTO(Long id, String invoiceNo, Long vendorId, String vendorName, VendorStatus vendorStatus, String billTo, BigDecimal amount, LocalDate invoiceDate, LocalDate dueDate) {
        this.id = id;
        this.invoiceNo = invoiceNo;
        this.vendorId = vendorId;
        this.vendorName = vendorName;
        this.vendorStatus = vendorStatus;
        this.billTo = billTo;
        this.amount = amount;
        this.invoiceDate = invoiceDate;
        this.dueDate = dueDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public VendorStatus getVendorStatus() {
        return vendorStatus;
    }

    public void setVendorStatus(VendorStatus vendorStatus) {
        this.vendorStatus = vendorStatus;
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

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getInvoiceNum() {
        return invoiceNo;
    }

    public void setInvoiceNum(String invoiceNum) {
        this.invoiceNo = invoiceNum;
    }
}
