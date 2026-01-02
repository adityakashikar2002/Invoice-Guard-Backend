package com.springboot.learning.invoiceguard.dto;

public class VendorResponseDTO {
    private Long id;
    private String name;
    private String registrationNo;
    private String status;

    public VendorResponseDTO() {

    }

    public VendorResponseDTO(Long id, String name, String registrationNo, String status) {
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

    public void setStatus(String status) {
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

    public String getStatus() {
        return status;
    }
}
