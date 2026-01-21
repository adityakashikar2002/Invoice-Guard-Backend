package com.springboot.learning.invoiceguard.dto;

import jakarta.validation.constraints.NotBlank;

public class VendorRequestDTO {

    @NotBlank
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
