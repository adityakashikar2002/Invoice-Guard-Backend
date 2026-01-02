package com.springboot.learning.invoiceguard.controller;

import com.springboot.learning.invoiceguard.dto.VendorRequestDTO;
import com.springboot.learning.invoiceguard.dto.VendorResponseDTO;
import com.springboot.learning.invoiceguard.model.Vendor;
import com.springboot.learning.invoiceguard.service.VendorService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    private final VendorService vendorService;

    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    @PostMapping
    public VendorResponseDTO createVendor(@RequestBody VendorRequestDTO request) {
        // Just calling the service and returning the result
        return vendorService.registerVendor(request);
    }

    @GetMapping("/{id}")
    public Vendor getById(@PathVariable Long id) {
        return vendorService.getVendorById(id);
    }
}
