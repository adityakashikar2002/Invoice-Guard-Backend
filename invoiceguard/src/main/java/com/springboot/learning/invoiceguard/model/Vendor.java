package com.springboot.learning.invoiceguard.model;

import jakarta.persistence.*;

@Entity
public class Vendor {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String regisNo;

    private String vendorName;

    @Enumerated(EnumType.STRING)
    private VendorStatus status;

    public Vendor() {

    }

    public Vendor(String regisNo, String vendorName, VendorStatus status) {
        this.regisNo = regisNo;
        this.vendorName = vendorName;
        this.status = status;
    }


    public Long getVendorId() {
        return id;
    }

    public String getRegisNo() {
        return regisNo;
    }

    public void setRegisNo(String regisNo) {
        this.regisNo = regisNo;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public VendorStatus getStatus() {
        return status;
    }

    public void setStatus(VendorStatus status) {
        this.status = status;
    }
}
