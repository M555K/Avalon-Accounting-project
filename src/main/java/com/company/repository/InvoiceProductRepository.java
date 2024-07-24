package com.company.repository;

import com.company.entity.InvoiceProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceProductRepository extends JpaRepository<InvoiceProduct,Long> {

    Optional<InvoiceProduct> findById(Long id);

}
