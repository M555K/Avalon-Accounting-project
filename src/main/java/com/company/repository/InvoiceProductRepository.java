package com.company.repository;

import com.company.entity.Company;
import com.company.entity.Invoice;
import com.company.entity.InvoiceProduct;
import com.company.enums.InvoiceStatus;
import com.company.enums.InvoiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceProductRepository extends JpaRepository<InvoiceProduct, Long> {

    Optional<InvoiceProduct> findById(Long id);

    List<InvoiceProduct> findAllByInvoice(Invoice invoice);

    List<InvoiceProduct> findAllByInvoice_Id(Long id);

    List<InvoiceProduct> findAllByInvoice_InvoiceStatusAndInvoice_Company(InvoiceStatus invoiceStatus, Company company);

    List<InvoiceProduct> findAllInvoiceProductByProductId(Long id);
}
