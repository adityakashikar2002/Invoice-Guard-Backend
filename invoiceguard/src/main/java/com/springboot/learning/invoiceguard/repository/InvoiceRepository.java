package com.springboot.learning.invoiceguard.repository;

import com.springboot.learning.invoiceguard.model.Invoice;
import com.springboot.learning.invoiceguard.model.InvoiceAudit;
import com.springboot.learning.invoiceguard.model.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long>, JpaSpecificationExecutor<Invoice> {

    boolean existsByInvoiceNumberAndVendorId(String invoiceNumber, Long vendorId);

    Page<Invoice> findAll(Specification<Invoice> spec, Pageable pageable);

//    Page<Invoice> findByStatus(InvoiceStatus status, Pageable pageable);
//
//    Page<Invoice> findByVendorId(Long vendorId, Pageable pageable);
//
//    Page<Invoice> findByInvoiceDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
}
