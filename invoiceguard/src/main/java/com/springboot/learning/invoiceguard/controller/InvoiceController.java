package com.springboot.learning.invoiceguard.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InvoiceController {

    @GetMapping("/test")
    public String testInvoice() {
        return "InvoiceGuard is running !!";
    }
}
