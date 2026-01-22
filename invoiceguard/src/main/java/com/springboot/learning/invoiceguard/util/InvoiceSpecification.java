package com.springboot.learning.invoiceguard.util;

import com.springboot.learning.invoiceguard.model.Invoice;
import com.springboot.learning.invoiceguard.model.InvoiceStatus;
import com.springboot.learning.invoiceguard.model.Vendor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class InvoiceSpecification {

    public static Specification<Invoice> hasStatus(InvoiceStatus status) {
        return ((root, query, criteriaBuilder) ->
                status == null ? null : criteriaBuilder.equal(root.get("status"), status));
    }

    public static Specification<Invoice> hasVendorId(Long vendorId) {
        return ((root, query, criteriaBuilder) ->
                vendorId == null ? null : criteriaBuilder.equal(root.get("vendor").get("id"), vendorId));
    }

    public static Specification<Invoice> datedBetween(LocalDate startDate, LocalDate endDate) {
        return ((root, query, criteriaBuilder) -> {
            if (startDate != null && endDate != null)
                return criteriaBuilder.between(root.get("invoiceDate"), startDate, endDate);
            else if (startDate != null)
                return criteriaBuilder.greaterThanOrEqualTo(root.get("invoiceDate"), startDate);
            else if (endDate != null)
                return criteriaBuilder.lessThanOrEqualTo(root.get("invoiceDate"), endDate);
            else
                return null;
        });

    }
}
