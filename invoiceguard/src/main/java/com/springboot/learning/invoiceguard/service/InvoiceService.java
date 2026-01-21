package com.springboot.learning.invoiceguard.service;

import com.springboot.learning.invoiceguard.dto.InvoiceActionResponseDTO;
import com.springboot.learning.invoiceguard.dto.InvoiceCreationRequestDTO;
import com.springboot.learning.invoiceguard.dto.InvoiceResponseDTO;
import com.springboot.learning.invoiceguard.exception.*;
import com.springboot.learning.invoiceguard.model.*;
import com.springboot.learning.invoiceguard.repository.InvoiceAuditRepository;
import com.springboot.learning.invoiceguard.repository.InvoiceRepository;
import com.springboot.learning.invoiceguard.repository.VendorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {
    private final BigDecimal AUTO_APPROVAL_LIMIT = new BigDecimal("50000");

    private final InvoiceRepository invoiceRepository;
    private final VendorRepository vendorRepository;
    private final InvoiceAuditRepository invoiceAuditRepository;

    public InvoiceService(InvoiceRepository invoiceRepository, VendorRepository vendorRepository, InvoiceAuditRepository invoiceAuditRepository) {
        this.invoiceRepository = invoiceRepository;
        this.vendorRepository = vendorRepository;
        this.invoiceAuditRepository = invoiceAuditRepository;
    }

    public InvoiceResponseDTO generateInvoice(InvoiceCreationRequestDTO request) {


        // Fetch Vendor By Id
        Optional<Vendor> v = vendorRepository.findById(request.getVendorId());

        if(v.isEmpty())
            throw new VendorNotFoundException("No Vendor Found !!");

        Vendor vendor = v.get();


        // Check Vendor Status
        if(vendor.getStatus().equals(VendorStatus.BLOCKED))
            throw new BlockedVendorException("Vendor is Blocked !! Cannot Create Invoice");

        // Duplicate Invoice Check
        if(invoiceRepository.existsByInvoiceNumberAndVendorId(request.getInvoiceNo(), vendor.getVendorId()))
            throw new DuplicateInvoiceException("Invoice " + request.getInvoiceNo() + " already exists for Vendor: " + vendor.getVendorName());

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
            throw new InvoiceNotFoundException("No Invoice Found !!");

        // Check Current Status of Invoice and Vendor
        Invoice invoice = inv.get();

        if(invoice.getVendor().getStatus().equals(VendorStatus.BLOCKED))
            throw new BlockedVendorException("Only Active Vendor's Invoices can be submitted");

        return invoice;
    }
    public InvoiceActionResponseDTO submitStatus(Long id) {
        // Check if Invoice Exists
        Invoice invoice = findAndValidateInvoice(id);

        if(invoice.getStatus() != InvoiceStatus.CREATED)
            throw new InvoiceStatusException("Only Created Invoices can be submitted");

        // Auto-approve check
        boolean isAutoApproved = invoice.getAmount().compareTo(AUTO_APPROVAL_LIMIT) <= 0;

        InvoiceStatus targetStatus = isAutoApproved ? InvoiceStatus.APPROVED : InvoiceStatus.SUBMITTED;
        String message = isAutoApproved ? "Invoice Auto-Approved" : "Invoice submitted for Approval";

        InvoiceStatus statusBeforeChange = invoice.getStatus();

        // Update Status and Save Invoice
        invoice.setStatus(targetStatus);
        invoiceRepository.save(invoice);

        // Create and Save Audit
        InvoiceAudit invoiceAudit = new InvoiceAudit(invoice.getId(), statusBeforeChange, targetStatus, LocalDateTime.now(), InvoiceAction.SUBMIT);
        invoiceAuditRepository.save(invoiceAudit);

        return new InvoiceActionResponseDTO(message, id, targetStatus);
    }

    public InvoiceActionResponseDTO approveStatus(Long id) {
        // Check if Invoice Exists
        Invoice invoice = findAndValidateInvoice(id);

        if(invoice.getStatus() != InvoiceStatus.SUBMITTED)
            throw new InvoiceStatusException("Only Submitted Invoices can be processed");

        InvoiceStatus statusBeforeChange = invoice.getStatus();

        // Update Status
        invoice.setStatus(InvoiceStatus.APPROVED);

        invoiceRepository.save(invoice);

        // Create and Save Audit
        InvoiceAudit invoiceAudit = new InvoiceAudit(invoice.getId(), statusBeforeChange, InvoiceStatus.APPROVED, LocalDateTime.now(), InvoiceAction.APPROVE);
        invoiceAuditRepository.save(invoiceAudit);

        return new InvoiceActionResponseDTO("Invoice approved successfully for payment!!", id, InvoiceStatus.APPROVED);
    }

    public InvoiceActionResponseDTO rejectStatus(Long id) {
        // Check if Invoice Exists
        Invoice invoice = findAndValidateInvoice(id);

        if(invoice.getStatus() != InvoiceStatus.SUBMITTED)
            throw new InvoiceStatusException("Only Submitted Invoices can be processed");

        InvoiceStatus statusBeforeChange = invoice.getStatus();

        // Update Status
        invoice.setStatus(InvoiceStatus.REJECTED);

        invoiceRepository.save(invoice);

        // Create and Save Audit
        InvoiceAudit invoiceAudit = new InvoiceAudit(invoice.getId(), statusBeforeChange, InvoiceStatus.REJECTED, LocalDateTime.now(), InvoiceAction.REJECT);
        invoiceAuditRepository.save(invoiceAudit);

        return new InvoiceActionResponseDTO("Invoice rejected for payment!!", id, InvoiceStatus.REJECTED);
    }

    public InvoiceActionResponseDTO payStatus(Long id) {
        // Check if Invoice Exists
        Invoice invoice = findAndValidateInvoice(id);

        if(invoice.getStatus() != InvoiceStatus.APPROVED)
            throw new InvoiceStatusException("Only Approved Invoices can be processed for payment");

        InvoiceStatus statusBeforeChange = invoice.getStatus();

        // Update Status
        invoice.setStatus(InvoiceStatus.PAID);

        invoiceRepository.save(invoice);

        // Create and Save Audit
        InvoiceAudit invoiceAudit = new InvoiceAudit(invoice.getId(), statusBeforeChange, InvoiceStatus.PAID, LocalDateTime.now(), InvoiceAction.PAY);
        invoiceAuditRepository.save(invoiceAudit);

        return new InvoiceActionResponseDTO("Invoice payment successful", id, InvoiceStatus.PAID);
    }

    public List<InvoiceAudit> auditList(Long id) {
        return invoiceAuditRepository.findByInvoiceIdOrderByTimeStampDesc(id);
    }

    private List<InvoiceResponseDTO> convertToInvoiceResponse(List<Invoice> invoices) {
        List<InvoiceResponseDTO> invoicesList = new ArrayList<>();

        for(Invoice invoice : invoices) {
            Vendor vendor = invoice.getVendor();

            invoicesList.add(new InvoiceResponseDTO(invoice.getId(), invoice.getInvoiceNumber(),
                    vendor.getVendorId(), vendor.getVendorName(), vendor.getStatus(),
                    invoice.getBillTo(),invoice.getAmount(), invoice.getInvoiceDate(),invoice.getDueDate()));
        }

        return invoicesList;
    }

    public Page<InvoiceResponseDTO> getInvoiceByStatus(InvoiceStatus status, Pageable pageable) {
        Page<Invoice> invoices = invoiceRepository.findByStatus(status, pageable);

        List<InvoiceResponseDTO> invLists = convertToInvoiceResponse(invoices.getContent());

        return new PageImpl<>(invLists, pageable, invoices.getTotalElements());
    }

    public Page<InvoiceResponseDTO> getInvoiceByVendorId(Long vendorId, Pageable pageable) {
        Page<Invoice> invoices = invoiceRepository.findByVendorId(vendorId, pageable);

        List<InvoiceResponseDTO> invLists = convertToInvoiceResponse(invoices.getContent());

        return new PageImpl<>(invLists, pageable, invoices.getTotalElements());
    }

    public Page<InvoiceResponseDTO> getInvoices(Pageable pageable) {

        Page<Invoice> invoices = invoiceRepository.findAll(pageable);

        List<InvoiceResponseDTO> invLists = convertToInvoiceResponse(invoices.getContent());

        return new PageImpl<>(invLists, pageable, invoices.getTotalElements());
    }

    public Page<InvoiceResponseDTO> getInvoiceBetween(LocalDate start, LocalDate end, Pageable pageable) {

        Page<Invoice> invoices = invoiceRepository.findByInvoiceDateBetween(start, end, pageable);

        List<InvoiceResponseDTO> invLists = convertToInvoiceResponse(invoices.getContent());

        return new PageImpl<>(invLists, pageable, invoices.getTotalElements());
    }
}
