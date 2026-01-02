package com.springboot.learning.invoiceguard.service;

import com.springboot.learning.invoiceguard.dto.VendorRequestDTO;
import com.springboot.learning.invoiceguard.dto.VendorResponseDTO;
import com.springboot.learning.invoiceguard.model.Vendor;
import com.springboot.learning.invoiceguard.model.VendorStatus;
import com.springboot.learning.invoiceguard.repository.VendorRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class VendorService {

    private final VendorRepository vendorRepository;

    public VendorService(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    public VendorResponseDTO registerVendor(VendorRequestDTO request) {

        Vendor vendor = new Vendor(request.getName(), VendorStatus.ACTIVE);

        vendorRepository.save(vendor);

        VendorResponseDTO response = new VendorResponseDTO(vendor.getVendorId(), vendor.getVendorName(),
                vendor.getRegisNo(), vendor.getStatus());

        return response;
    }

    public Vendor getVendorById(Long id) {
        Optional<Vendor> vendor = vendorRepository.findById(id);

        if(!vendor.isPresent())
            throw new RuntimeException("No Vendor Found !!");

        return vendor.get();
    }
}
