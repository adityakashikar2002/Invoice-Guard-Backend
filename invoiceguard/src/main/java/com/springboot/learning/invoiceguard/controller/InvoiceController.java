package com.springboot.learning.invoiceguard.controller;

import com.springboot.learning.invoiceguard.dto.InvoiceCreationRequestDTO;
import com.springboot.learning.invoiceguard.dto.InvoiceResponseDTO;
import com.springboot.learning.invoiceguard.service.InvoiceService;
import org.springframework.web.bind.annotation.*;

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
}
