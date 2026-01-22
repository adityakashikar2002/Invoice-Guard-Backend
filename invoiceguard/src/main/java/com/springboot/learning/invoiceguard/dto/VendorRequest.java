package com.springboot.learning.invoiceguard.dto;

import jakarta.validation.constraints.NotBlank;

public class VendorRequest {

    @NotBlank
    private String name;

    public VendorRequest() {

    }

    public VendorRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
