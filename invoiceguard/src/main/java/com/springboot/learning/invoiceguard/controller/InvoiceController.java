package com.springboot.learning.invoiceguard.controller;

import com.springboot.learning.invoiceguard.dto.InvoiceActionResponseDTO;
import com.springboot.learning.invoiceguard.dto.InvoiceCreationRequestDTO;
import com.springboot.learning.invoiceguard.dto.InvoiceResponseDTO;
import com.springboot.learning.invoiceguard.model.InvoiceAction;
import com.springboot.learning.invoiceguard.model.InvoiceAudit;
import com.springboot.learning.invoiceguard.model.InvoiceStatus;
import com.springboot.learning.invoiceguard.service.InvoiceService;
import jakarta.validation.Valid;
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
    public InvoiceResponseDTO createInvoice( @Valid @RequestBody InvoiceCreationRequestDTO request) {
        return invoiceService.generateInvoice(request);
    }

    @PatchMapping("/{id}/status")
    public InvoiceActionResponseDTO updateInvoiceStatus(@PathVariable Long id, @Valid @RequestBody InvoiceAction action) {
        return invoiceService.updateStatus(id,action);
    }

    @GetMapping("/{id}/audit")
    public List<InvoiceAudit> auditLogs(@PathVariable Long id) {
        return invoiceService.auditList(id);
    }

    @GetMapping("/search")
    public Page<InvoiceResponseDTO> searchInvoices(
            @RequestParam(value = "status", required = false)InvoiceStatus status,
            @RequestParam(value = "vendorId", required = false)Long vendorId,
            @RequestParam(value = "startDate", required = false)LocalDate start,
            @RequestParam(value = "endDate", required = false) LocalDate end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
            )
    {
        Pageable pageable = PageRequest.of(page, size);

        return invoiceService.searchInvoices(status, vendorId, start, end, pageable);
        
    }

}
