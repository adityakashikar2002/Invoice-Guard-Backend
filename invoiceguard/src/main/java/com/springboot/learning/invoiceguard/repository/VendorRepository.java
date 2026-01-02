package com.springboot.learning.invoiceguard.repository;

import com.springboot.learning.invoiceguard.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendorRepository extends JpaRepository<Vendor, Long> {

}
