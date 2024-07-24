package com.company.service.impl;

import com.company.dto.InvoiceDto;
import com.company.entity.Invoice;
import com.company.repository.InvoiceRepository;
import com.company.service.InvoiceService;
import com.company.util.MapperUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final MapperUtil mapperUtil;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, MapperUtil mapperUtil) {
        this.invoiceRepository = invoiceRepository;
        this.mapperUtil = mapperUtil;
    }


    @Override
    public List<InvoiceDto> listAllInvoicesInDescendingOrder() {
        return null;
    }

    @Override
    public InvoiceDto findById(Long id) {
        Invoice foundInvoice = invoiceRepository.findInvoiceById(id)
                .orElseThrow(() -> new RuntimeException("No Invoice Found!"));
        InvoiceDto invoiceDto = mapperUtil.convert(foundInvoice, new InvoiceDto());
        return invoiceDto;
    }
}

