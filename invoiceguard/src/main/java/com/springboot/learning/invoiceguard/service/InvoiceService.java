package com.springboot.learning.invoiceguard.service;

import com.springboot.learning.invoiceguard.dto.InvoiceActionResponseDTO;
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

    private Invoice findAndValidateInvoice(Long id) {
        Optional<Invoice> inv = invoiceRepository.findById(id);

        if(inv.isEmpty())
            throw new RuntimeException("No Invoice Found !!");

        // Check Current Status of Invoice and Vendor
        Invoice invoice = inv.get();

        if(invoice.getVendor().getStatus().equals(VendorStatus.BLOCKED))
            throw new RuntimeException("Only Active Vendor's Invoices can be submitted");

        return invoice;
    }
    public InvoiceActionResponseDTO submitStatus(Long id) {
        // Check if Invoice Exists
        Invoice invoice = findAndValidateInvoice(id);

        if(invoice.getStatus() != InvoiceStatus.CREATED)
            throw new RuntimeException("Only Created Invoices can be submitted");

        // Update Status
        invoice.setStatus(InvoiceStatus.SUBMITTED);

        invoiceRepository.save(invoice);

        return new InvoiceActionResponseDTO("Invoice submitted successfully for approval !!", id, InvoiceStatus.SUBMITTED);
    }

    public InvoiceActionResponseDTO approveStatus(Long id) {
        // Check if Invoice Exists
        Invoice invoice = findAndValidateInvoice(id);

        if(invoice.getStatus() != InvoiceStatus.SUBMITTED)
            throw new RuntimeException("Only Submitted Invoices can be processed");

        // Update Status
        invoice.setStatus(InvoiceStatus.APPROVED);

        invoiceRepository.save(invoice);

        return new InvoiceActionResponseDTO("Invoice approved successfully for payment!!", id, InvoiceStatus.APPROVED);
    }

    public InvoiceActionResponseDTO rejectStatus(Long id) {
        // Check if Invoice Exists
        Invoice invoice = findAndValidateInvoice(id);

        if(invoice.getStatus() != InvoiceStatus.SUBMITTED)
            throw new RuntimeException("Only Submitted Invoices can be processed");

        // Update Status
        invoice.setStatus(InvoiceStatus.REJECTED);

        invoiceRepository.save(invoice);

        return new InvoiceActionResponseDTO("Invoice rejected for payment!!", id, InvoiceStatus.REJECTED);
    }

    public InvoiceActionResponseDTO payStatus(Long id) {
        // Check if Invoice Exists
        Invoice invoice = findAndValidateInvoice(id);

        if(invoice.getStatus() != InvoiceStatus.APPROVED)
            throw new RuntimeException("Only Approved Invoices can be processed for payment");

        // Update Status
        invoice.setStatus(InvoiceStatus.PAID);

        invoiceRepository.save(invoice);

        return new InvoiceActionResponseDTO("Invoice payment successful", id, InvoiceStatus.PAID);
    }
}
