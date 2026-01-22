package com.springboot.learning.invoiceguard.controller;

import com.springboot.learning.invoiceguard.dto.VendorRequest;
import com.springboot.learning.invoiceguard.dto.VendorResponse;
import com.springboot.learning.invoiceguard.service.VendorService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    private final VendorService vendorService;

    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    @PostMapping("/register")
    public VendorResponse createVendor(@Valid @RequestBody VendorRequest request) {
        // Just calling the service and returning the result
        return vendorService.registerVendor(request);
    }

    @GetMapping("/{id}")
    public VendorResponse getById(@PathVariable Long id) {
        return vendorService.getVendorById(id);
    }

    @GetMapping("/all")
    public List<VendorResponse> getAll() {
        return vendorService.getAllVendors();
    }
}
