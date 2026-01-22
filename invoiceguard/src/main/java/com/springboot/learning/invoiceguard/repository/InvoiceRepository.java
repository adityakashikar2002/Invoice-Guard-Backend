package com.springboot.learning.invoiceguard.repository;

import com.springboot.learning.invoiceguard.model.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InvoiceRepository extends JpaRepository<Invoice, Long>, JpaSpecificationExecutor<Invoice> {

    boolean existsByInvoiceNumberAndVendorId(String invoiceNumber, Long vendorId);

    Page<Invoice> findAll(Specification<Invoice> spec, Pageable pageable);

}
