package com.springboot.learning.invoiceguard.service;

import com.springboot.learning.invoiceguard.dto.VendorRequest;
import com.springboot.learning.invoiceguard.dto.VendorResponse;
import com.springboot.learning.invoiceguard.exception.VendorNotFoundException;
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

    public VendorResponse registerVendor(VendorRequest request) {

        Vendor vendor = new Vendor(request.getName(), VendorStatus.ACTIVE);

        Vendor savedVendor = vendorRepository.save(vendor);

        return new VendorResponse(
                savedVendor.getVendorId(),
                savedVendor.getVendorName(),
                savedVendor.getRegisNo(),
                savedVendor.getStatus()
        );
    }

    public VendorResponse getVendorById(Long id) {
        Optional<Vendor> vendor = vendorRepository.findById(id);

        if(vendor.isEmpty())
            throw new VendorNotFoundException("No Vendor Found !!");

        return new VendorResponse(vendor.get().getVendorId(), vendor.get().getVendorName(),
                vendor.get().getRegisNo(), vendor.get().getStatus());
    }

    public List<VendorResponse> getAllVendors() {
        List<Vendor> vendors = vendorRepository.findAll();

        return vendors.stream().map(v -> new VendorResponse(
                v.getVendorId(),
                v.getVendorName(),
                v.getRegisNo(),
                v.getStatus()
        )).toList();
    }

}
