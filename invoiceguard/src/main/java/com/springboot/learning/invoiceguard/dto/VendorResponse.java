package com.springboot.learning.invoiceguard.dto;

import com.springboot.learning.invoiceguard.model.VendorStatus;

public class VendorResponse {
    private Long id;
    private String name;
    private String registrationNo;
    private VendorStatus status;

    public VendorResponse() {

    }

    public VendorResponse(Long id, String name, String registrationNo, VendorStatus status) {
        this.id = id;
        this.name = name;
        this.registrationNo = registrationNo;
        this.status = status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    public void setStatus(VendorStatus status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRegistrationNo() {
        return registrationNo;
    }

    public VendorStatus getStatus() {
        return status;
    }
}
