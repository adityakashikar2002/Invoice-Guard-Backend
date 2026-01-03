package com.springboot.learning.invoiceguard.service;

import com.springboot.learning.invoiceguard.dto.VendorRequestDTO;
import com.springboot.learning.invoiceguard.dto.VendorResponseDTO;
import com.springboot.learning.invoiceguard.model.Vendor;
import com.springboot.learning.invoiceguard.model.VendorStatus;
import com.springboot.learning.invoiceguard.repository.VendorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VendorService {

    private final VendorRepository vendorRepository;

    public VendorService(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    public VendorResponseDTO registerVendor(VendorRequestDTO request) {

        Vendor vendor = new Vendor(request.getName(), VendorStatus.ACTIVE);

        vendorRepository.save(vendor);

        Vendor savedVendor = vendorRepository.save(vendor);

        return new VendorResponseDTO(
                savedVendor.getVendorId(),
                savedVendor.getVendorName(),
                savedVendor.getRegisNo(),
                savedVendor.getStatus()
        );
    }

    public VendorResponseDTO getVendorById(Long id) {
        Optional<Vendor> vendor = vendorRepository.findById(id);

        if(vendor.isEmpty())
            throw new RuntimeException("No Vendor Found !!");

        return new VendorResponseDTO(vendor.get().getVendorId(), vendor.get().getVendorName(),
                vendor.get().getRegisNo(), vendor.get().getStatus());
    }

    public List<VendorResponseDTO> getAllVendors() {
        List<Vendor> vendors = vendorRepository.findAll();

        return vendors.stream().map(v -> new VendorResponseDTO(
                v.getVendorId(),
                v.getVendorName(),
                v.getRegisNo(),
                v.getStatus()
        )).toList();
    }

}
