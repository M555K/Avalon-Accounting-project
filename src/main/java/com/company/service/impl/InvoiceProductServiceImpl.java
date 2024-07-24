package com.company.service.impl;

import com.company.service.InvoiceProductService;
import com.company.util.MapperUtil;
import org.springframework.stereotype.Service;

@Service
public class InvoiceProductServiceImpl implements InvoiceProductService {

    private final InvoiceProductRepository invoiceProductRepository;
    private final MapperUtil mapperUtil;

    public InvoiceProductServiceImpl(InvoiceProductRepository invoiceProductRepository, MapperUtil mapperUtil) {
        this.invoiceProductRepository = invoiceProductRepository;
        this.mapperUtil = mapperUtil;
    }


    @Override
    public InvoiceProductDto findInvoiceProductById(Long id) {
        InvoiceProduct foundInvoiceProduct = invoiceProductRepository.findById(id)
                .orElseThrow(()->new RuntimeException("no invoiceProduct found"));
        return mapperUtil.convert(foundInvoiceProduct, new InvoiceProductDto());
    }
}

