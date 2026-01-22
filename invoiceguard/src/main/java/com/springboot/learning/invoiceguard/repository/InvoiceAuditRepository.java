package com.springboot.learning.invoiceguard.repository;

import com.springboot.learning.invoiceguard.model.InvoiceAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceAuditRepository extends JpaRepository<InvoiceAudit, Long> {

    // Find all audits for a specific invoice, sorted by time (newest last)
    List<InvoiceAudit> findByInvoiceIdOrderByTimeStampDesc(Long invoiceId);
}
