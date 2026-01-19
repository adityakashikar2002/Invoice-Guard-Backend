package com.springboot.learning.invoiceguard.controller;

import com.springboot.learning.invoiceguard.dto.InvoiceActionResponseDTO;
import com.springboot.learning.invoiceguard.dto.InvoiceCreationRequestDTO;
import com.springboot.learning.invoiceguard.dto.InvoiceResponseDTO;
import com.springboot.learning.invoiceguard.model.InvoiceAudit;
import com.springboot.learning.invoiceguard.model.InvoiceStatus;
import com.springboot.learning.invoiceguard.service.InvoiceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping("/create")
    public InvoiceResponseDTO createInvoice(@RequestBody InvoiceCreationRequestDTO request) {
        return invoiceService.generateInvoice(request);
    }

    @PostMapping("/{id}/submit")
    public InvoiceActionResponseDTO submitInvoice(@PathVariable Long id) {
        return invoiceService.submitStatus(id);
    }

    @PostMapping("/{id}/approve")
    public InvoiceActionResponseDTO approveInvoice(@PathVariable Long id) {
        return invoiceService.approveStatus(id);
    }

    @PostMapping("/{id}/reject")
    public InvoiceActionResponseDTO rejectInvoice(@PathVariable Long id) {
        return invoiceService.rejectStatus(id);
    }

    @PostMapping("/{id}/pay")
    public InvoiceActionResponseDTO payInvoice(@PathVariable Long id) {
        return invoiceService.payStatus(id);
    }

    @GetMapping("/{id}/audit")
    public List<InvoiceAudit> auditLogs(@PathVariable Long id) {
        return invoiceService.auditList(id);
    }

    @GetMapping("/search")
    public Page<InvoiceResponseDTO> searchInvoices(
            @RequestParam(value = "status", required = false)InvoiceStatus status,
            @RequestParam(value = "vectorId", required = false)Long vendorId,
            @RequestParam(value = "startDate", required = false)LocalDate start,
            @RequestParam(value = "endDate", required = false) LocalDate end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
            )
    {
        Pageable pageable = PageRequest.of(page, size);

        if (status != null)
            return invoiceService.getInvoiceByStatus(status, pageable);
        else if (vendorId != null)
            return invoiceService.getInvoiceByVendorId(vendorId, pageable);
        else if (start != null && end != null)
            return invoiceService.getInvoiceBetween(start, end, pageable);
        else
            return invoiceService.getInvoices(pageable);
    }

}
