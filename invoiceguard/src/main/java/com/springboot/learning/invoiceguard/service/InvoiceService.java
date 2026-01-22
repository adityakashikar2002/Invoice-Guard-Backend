package com.springboot.learning.invoiceguard.service;

import com.springboot.learning.invoiceguard.dto.InvoiceActionRequest;
import com.springboot.learning.invoiceguard.dto.InvoiceActionResponse;
import com.springboot.learning.invoiceguard.dto.InvoiceCreationRequest;
import com.springboot.learning.invoiceguard.dto.InvoiceResponse;
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
import org.springframework.transaction.annotation.Transactional;

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


    public InvoiceResponse generateInvoice(InvoiceCreationRequest request) {


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
        return new InvoiceResponse(savedInvoice.getId(), savedInvoice.getInvoiceNumber(), vendor.getVendorId(), vendor.getVendorName(), vendor.getStatus(),
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
            throw new InvoiceStatusException("Only " + required.toString() + " invoices can be processed for transitions.");

        return target;
    }

    private String getTransitionMessage(InvoiceAction action, InvoiceStatus targetStatus) {
        if (action == InvoiceAction.SUBMIT && targetStatus == InvoiceStatus.APPROVED) {
            return "Invoice auto-approved based on threshold limit.";
        }

        return switch (action) {
            case SUBMIT -> "Invoice Submitted for review.";
            case APPROVE -> "Invoice Approved successfully.";
            case REJECT -> "Invoice has been Rejected.";
            case PAY -> "Payment processed successfully.";
            default -> "Invalid action !!";
        };
    }

    @Transactional
    public InvoiceActionResponse updateStatus(Long id, InvoiceActionRequest request) {
        // Check if Invoice Exists
        Invoice invoice = findAndValidateInvoice(id);

        // Acquire Old Status
        InvoiceStatus oldStatus = invoice.getStatus();
        InvoiceStatus targetStatus;

        InvoiceAction action = request.getAction();

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
                throw new InvalidInvoiceActionException("Transition State Not Supported");
        }

        // Action Completion Message
        String message = getTransitionMessage(action, targetStatus);

        // Save Invoice, Make Audit, Save Audit
        invoice.setStatus(targetStatus);
        Invoice savedInvoice = invoiceRepository.save(invoice);
        InvoiceAudit invoiceAudit = new InvoiceAudit(savedInvoice.getId(), oldStatus, targetStatus, LocalDateTime.now(), action);
        invoiceAuditRepository.save(invoiceAudit);

        return new InvoiceActionResponse(message, id, targetStatus);

    }

    public List<InvoiceAudit> auditList(Long id) {
        return invoiceAuditRepository.findByInvoiceIdOrderByTimeStampDesc(id);
    }

    private List<InvoiceResponse> convertToInvoiceResponse(List<Invoice> invoices) {
        List<InvoiceResponse> invoicesList = new ArrayList<>();

        for(Invoice invoice : invoices) {
            Vendor vendor = invoice.getVendor();

            invoicesList.add(new InvoiceResponse(invoice.getId(), invoice.getInvoiceNumber(),
                    vendor.getVendorId(), vendor.getVendorName(), vendor.getStatus(),
                    invoice.getBillTo(),invoice.getAmount(), invoice.getInvoiceDate(),invoice.getDueDate()));
        }

        return invoicesList;
    }

    private Page<InvoiceResponse> getInvoices(Specification<Invoice> spec, Pageable pageable) {

        Page<Invoice> invoices = invoiceRepository.findAll(spec, pageable);

        List<InvoiceResponse> invLists = convertToInvoiceResponse(invoices.getContent());

        return new PageImpl<>(invLists, pageable, invoices.getTotalElements());
    }

    public Page<InvoiceResponse> searchInvoices(InvoiceStatus status, Long vendorId,
                                                LocalDate startDate, LocalDate endDate, Pageable pageable) {

        Specification<Invoice> spec = Specification.
                where(InvoiceSpecification.hasStatus(status))
                .and(InvoiceSpecification.hasVendorId(vendorId))
                .and(InvoiceSpecification.datedBetween(startDate, endDate));

        return getInvoices(spec, pageable);
    }
}
