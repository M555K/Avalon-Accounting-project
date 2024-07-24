package com.company.service;

import com.company.dto.InvoiceDto;

import java.util.List;

public interface InvoiceService {

    List<InvoiceDto> listAllInvoicesInDescendingOrder();

    InvoiceDto findById(Long id);

}
