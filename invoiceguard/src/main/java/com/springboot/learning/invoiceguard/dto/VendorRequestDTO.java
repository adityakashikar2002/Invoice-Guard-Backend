package com.springboot.learning.invoiceguard.dto;

public class VendorRequestDTO {

    private String name;

    public VendorRequestDTO() {

    }

    public VendorRequestDTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
