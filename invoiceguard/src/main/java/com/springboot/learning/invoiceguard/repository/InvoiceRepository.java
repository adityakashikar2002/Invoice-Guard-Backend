package com.springboot.learning.invoiceguard.repository;

import com.springboot.learning.invoiceguard.model.Invoice;
import com.springboot.learning.invoiceguard.model.InvoiceAudit;
import com.springboot.learning.invoiceguard.model.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    boolean existsByInvoiceNumberAndVendorId(String invoiceNumber, Long vendorId);

    List<Invoice> findByStatus(InvoiceStatus status);

    List<Invoice> findByVendorId(Long vendorId);

    List<Invoice> findByInvoiceDateBetween(LocalDate startDate, LocalDate endDate);
}
