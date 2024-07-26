package com.company.service.impl;

import com.company.dto.InvoiceDto;
import com.company.dto.InvoiceProductDto;
import com.company.entity.Invoice;
import com.company.entity.InvoiceProduct;
import com.company.repository.InvoiceProductRepository;
import com.company.repository.InvoiceRepository;
import com.company.service.InvoiceProductService;
import com.company.service.InvoiceService;
import com.company.util.MapperUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceProductServiceImpl implements InvoiceProductService {

    private final InvoiceProductRepository invoiceProductRepository;
    private final MapperUtil mapperUtil;
    private final InvoiceService invoiceService;

    public InvoiceProductServiceImpl(InvoiceProductRepository invoiceProductRepository, MapperUtil mapperUtil, InvoiceRepository invoiceRepository, InvoiceService invoiceService) {
        this.invoiceProductRepository = invoiceProductRepository;
        this.mapperUtil = mapperUtil;
        this.invoiceService = invoiceService;
    }


    @Override
    public InvoiceProductDto findInvoiceProductById(Long id) {
        InvoiceProduct foundInvoiceProduct = invoiceProductRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("no invoiceProduct found"));
        return mapperUtil.convert(foundInvoiceProduct, new InvoiceProductDto());
    }

    @Override
    public InvoiceProductDto save(InvoiceProductDto invoiceProductDto, Long invoiceId) {

        InvoiceDto foundInvoice = invoiceService.findById(invoiceId);
        Invoice convertedInvoice = mapperUtil.convert(foundInvoice, new Invoice());
        InvoiceProduct invoiceProduct = mapperUtil.convert(invoiceProductDto, new InvoiceProduct());

        invoiceProduct.setInvoice(convertedInvoice);
        invoiceProductRepository.save(invoiceProduct);
        return mapperUtil.convert(convertedInvoice, new InvoiceProductDto());
    }

    @Override
    public List<InvoiceProductDto> getAllInvoiceProducts(Long id) {

        Invoice foundInvoice = mapperUtil.convert(invoiceService.findById(id), new Invoice());
        invoiceProductRepository.findAllByInvoice(foundInvoice);
        return invoiceProductRepository
                .findAllByInvoice(foundInvoice)
                .stream()
                .sorted(Comparator.comparing((InvoiceProduct each) -> each.getInvoice().getInvoiceNo()).reversed())
                .map(each -> mapperUtil.convert(each, new InvoiceProductDto()))
                .peek(dto -> dto.setTotal(dto.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity() * (dto.getTax() + 100) / 100d))))
                .collect(Collectors.toList());
    }
}

