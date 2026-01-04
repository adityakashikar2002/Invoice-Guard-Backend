package com.springboot.learning.invoiceguard.service;

import com.springboot.learning.invoiceguard.dto.InvoiceCreationRequestDTO;
import com.springboot.learning.invoiceguard.dto.InvoiceResponseDTO;
import com.springboot.learning.invoiceguard.model.Invoice;
import com.springboot.learning.invoiceguard.model.InvoiceStatus;
import com.springboot.learning.invoiceguard.model.Vendor;
import com.springboot.learning.invoiceguard.model.VendorStatus;
import com.springboot.learning.invoiceguard.repository.InvoiceRepository;
import com.springboot.learning.invoiceguard.repository.VendorRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final VendorRepository vendorRepository;

    public InvoiceService(InvoiceRepository invoiceRepository, VendorRepository vendorRepository) {
        this.invoiceRepository = invoiceRepository;
        this.vendorRepository = vendorRepository;
    }

    public InvoiceResponseDTO generateInvoice(InvoiceCreationRequestDTO request) {


        // Fetch Vendor By Id
        Optional<Vendor> v = vendorRepository.findById(request.getVendorId());

        if(v.isEmpty())
            throw new RuntimeException("No Vendor Found !!");

        Vendor vendor = v.get();


        // Check Vendor Status
        if(vendor.getStatus().equals(VendorStatus.BLOCKED))
            throw new RuntimeException("Vendor is Blocked !! Cannot Create Invoice");

        // Duplicate Invoice Check
        if(invoiceRepository.existsByInvoiceNumberAndVendorId(request.getInvoiceNo(), vendor.getVendorId()))
            throw new RuntimeException("Invoice " + request.getInvoiceNo() + " already exists for Vendor: " + vendor.getVendorName());

        // Create Invoice
        Invoice invoice = new Invoice(request.getInvoiceNo(), vendor, request.getBillTo(), request.getInvoiceDate(),
                request.getDueDate(), request.getAmount(), InvoiceStatus.CREATED);

        // Save Invoice
        Invoice savedInvoice = invoiceRepository.save(invoice);

        // Create Response
        return new InvoiceResponseDTO(savedInvoice.getId(), savedInvoice.getInvoiceNumber(), vendor.getVendorId(), vendor.getVendorName(), vendor.getStatus(),
                savedInvoice.getBillTo(), savedInvoice.getAmount(), savedInvoice.getInvoiceDate(), savedInvoice.getDueDate());
    }


}
