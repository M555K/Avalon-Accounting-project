package com.company.service;

import com.company.dto.InvoiceProductDto;

import java.util.List;

public interface InvoiceProductService {

    InvoiceProductDto findInvoiceProductById (Long id);

    InvoiceProductDto save(InvoiceProductDto invoiceProductDto,Long invoiceId);

    List<InvoiceProductDto> getAllInvoiceProducts(Long id);



}
