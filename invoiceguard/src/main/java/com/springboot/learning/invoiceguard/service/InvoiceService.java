package com.springboot.learning.invoiceguard.service;

import com.springboot.learning.invoiceguard.dto.InvoiceActionResponseDTO;
import com.springboot.learning.invoiceguard.dto.InvoiceCreationRequestDTO;
import com.springboot.learning.invoiceguard.dto.InvoiceResponseDTO;
import com.springboot.learning.invoiceguard.exception.*;
import com.springboot.learning.invoiceguard.model.*;
import com.springboot.learning.invoiceguard.repository.InvoiceAuditRepository;
import com.springboot.learning.invoiceguard.repository.InvoiceRepository;
import com.springboot.learning.invoiceguard.repository.VendorRepository;
import com.springboot.learning.invoiceguard.util.InvoiceSpecification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final VendorRepository vendorRepository;
    private final InvoiceAuditRepository invoiceAuditRepository;

    public InvoiceService(InvoiceRepository invoiceRepository, VendorRepository vendorRepository, InvoiceAuditRepository invoiceAuditRepository) {
        this.invoiceRepository = invoiceRepository;
        this.vendorRepository = vendorRepository;
        this.invoiceAuditRepository = invoiceAuditRepository;
    }

    @Value("${invoice.auto-approval-limit}")
    private BigDecimal AUTO_APPROVAL_LIMIT;


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

    private InvoiceStatus determineSubmitTarget(Invoice invoice) {

        boolean isAutoApproved = invoice.getAmount().compareTo(AUTO_APPROVAL_LIMIT) <= 0;

        return isAutoApproved ? InvoiceStatus.APPROVED : InvoiceStatus.SUBMITTED;
    }

    private InvoiceStatus validateAndGetStatus(InvoiceStatus oldStatus, InvoiceStatus required, InvoiceStatus target) {
        if(oldStatus != required)
            throw new InvoiceStatusException("Only " + required.toString() + " can be processed for transitions.");

        return target;
    }

    public InvoiceActionResponseDTO updateStatus(Long id, InvoiceAction action) {
        // Check if Invoice Exists
        Invoice invoice = findAndValidateInvoice(id);

        // Acquire Old Status
        InvoiceStatus oldStatus = invoice.getStatus();
        InvoiceStatus targetStatus;

        switch (action) {
            case InvoiceAction.SUBMIT:
                if(oldStatus != InvoiceStatus.CREATED)
                    throw new InvoiceStatusException("Only Created Invoices can be submitted");
                targetStatus = determineSubmitTarget(invoice); // AUTO-APPROVE CHECK
                break;
            case InvoiceAction.APPROVE:
                targetStatus = validateAndGetStatus(oldStatus, InvoiceStatus.SUBMITTED,InvoiceStatus.APPROVED);
                break;
            case InvoiceAction.REJECT:
                targetStatus = validateAndGetStatus(oldStatus, InvoiceStatus.SUBMITTED,InvoiceStatus.REJECTED);
                break;
            case InvoiceAction.PAY:
                targetStatus = validateAndGetStatus(oldStatus, InvoiceStatus.APPROVED, InvoiceStatus.PAID);
                break;
            default:
                throw new InvalidInvoiceAction("Transition State Not Supported");
        }

        // Action Completion Message
        String message = (action == InvoiceAction.SUBMIT && targetStatus == InvoiceStatus.APPROVED)
                ? "Invoice Auto-Approved"
                : "Action " + action + " completed successfully";

        // Save Invoice, Make Audit, Save Audit
        invoiceRepository.save(invoice);
        InvoiceAudit invoiceAudit = new InvoiceAudit(invoice.getId(), oldStatus, targetStatus, LocalDateTime.now(), action);
        invoiceAuditRepository.save(invoiceAudit);

        return new InvoiceActionResponseDTO(message, id, targetStatus);

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

    private Page<InvoiceResponseDTO> getInvoices(Specification<Invoice> spec, Pageable pageable) {

        Page<Invoice> invoices = invoiceRepository.findAll(spec, pageable);

        List<InvoiceResponseDTO> invLists = convertToInvoiceResponse(invoices.getContent());

        return new PageImpl<>(invLists, pageable, invoices.getTotalElements());
    }

    public Page<InvoiceResponseDTO> searchInvoices(InvoiceStatus status, Long vendorId,
                                    LocalDate startDate, LocalDate endDate, Pageable pageable) {

        Specification<Invoice> spec = Specification.
                where(InvoiceSpecification.hasStatus(status))
                .and(InvoiceSpecification.hasVendorId(vendorId))
                .and(InvoiceSpecification.datedBetween(startDate, endDate));

        return getInvoices(spec, pageable);
    }
}
