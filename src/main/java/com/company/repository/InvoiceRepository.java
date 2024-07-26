package com.company.repository;

import com.company.entity.Company;
import com.company.entity.Invoice;
import com.company.enums.InvoiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {


    Optional<Invoice> findInvoiceById(Long id);

    List<Invoice> findAllByCompanyAndInvoiceTypeOrderByInvoiceNoDesc(Company company, InvoiceType invoiceType);

    List<Invoice> findAllByInvoiceType(InvoiceType invoiceType);
}



