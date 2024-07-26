package com.company.service;

import com.company.dto.InvoiceDto;
import com.company.enums.InvoiceType;

import java.math.BigDecimal;
import java.util.List;

public interface InvoiceService {



    List<InvoiceDto> listAllByCompanyAndInvoiceType(InvoiceType invoiceType);


    InvoiceDto findById(Long id);

    List<InvoiceDto> listAllByInvoiceType(InvoiceType invoiceType);

    BigDecimal getTotalPriceOfInvoice(InvoiceDto invoiceDto);

    BigDecimal getTotalTaxOfInvoice(InvoiceDto invoiceDto);

}
