package com.company.service.impl;

import com.company.dto.CompanyDto;
import com.company.dto.InvoiceDto;
import com.company.dto.InvoiceProductDto;
import com.company.entity.Company;
import com.company.entity.Invoice;
import com.company.enums.InvoiceType;
import com.company.repository.InvoiceRepository;
import com.company.service.CompanyService;
import com.company.service.InvoiceProductService;
import com.company.service.InvoiceService;
import com.company.service.SecurityService;
import com.company.util.MapperUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final MapperUtil mapperUtil;
    private final CompanyService companyService;
    private final SecurityService securityService;
    private final InvoiceProductService invoiceProductService;


    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, MapperUtil mapperUtil, CompanyService companyService, SecurityService securityService,@Lazy InvoiceProductService invoiceProductService) {
        this.invoiceRepository = invoiceRepository;
        this.mapperUtil = mapperUtil;
        this.companyService = companyService;
        this.securityService = securityService;
        this.invoiceProductService = invoiceProductService;
    }


    @Override
    public List<InvoiceDto> listAllByCompanyAndInvoiceType(InvoiceType invoiceType) {

        return invoiceRepository.findAllByCompanyAndInvoiceTypeOrderByInvoiceNoDesc(mapperUtil.convert(getCompanyByLoggedInUser(), new Company()), invoiceType).stream()
                .sorted(Comparator.comparing(Invoice::getInvoiceNo))
                .map(each -> mapperUtil.convert(each, new InvoiceDto()))
                .peek(this::displayInvoiceDetails)
                .collect(Collectors.toList());
    }

    @Override
    public InvoiceDto findById(Long id) {
        Invoice foundInvoice = invoiceRepository.findInvoiceById(id)
                .orElseThrow(() -> new RuntimeException("No Invoice Found!"));
        InvoiceDto invoiceDto = mapperUtil.convert(foundInvoice, new InvoiceDto());
        return invoiceDto;
    }

    @Override
    public List<InvoiceDto> listAllByInvoiceType(InvoiceType invoiceType) {

        List<Invoice> foundInvoices = invoiceRepository.findAllByInvoiceType(invoiceType);
        return mapperUtil.convert(foundInvoices, new ArrayList<>());

    }

    @Override
    public BigDecimal getTotalPriceOfInvoice(InvoiceDto invoiceDto) {
        List<InvoiceProductDto> allInvoiceProductOfInvoice = invoiceProductService.getAllInvoiceProducts(invoiceDto.getId());

        return allInvoiceProductOfInvoice.stream()
                .map(eachProduct -> eachProduct.getPrice()
                        .multiply(BigDecimal.valueOf(eachProduct.getQuantity())))
                .reduce(BigDecimal::add).orElseThrow();
    }

    @Override
    public BigDecimal getTotalTaxOfInvoice(InvoiceDto invoiceDto) {
        List<InvoiceProductDto> allInvoiceProductOfInvoice = invoiceProductService.getAllInvoiceProducts(invoiceDto.getId());
        return allInvoiceProductOfInvoice.stream()
                .map(eachProduct -> eachProduct.getPrice()
                        .multiply(BigDecimal.valueOf(eachProduct.getQuantity() * eachProduct.getTax() / 100d))
                        .setScale(2, RoundingMode.HALF_UP)).reduce(BigDecimal::add).orElseThrow();
    }

    private CompanyDto getCompanyByLoggedInUser() {
        //  Company company = userCompany.getCurrentCompany();
        CompanyDto company = securityService.getLoggedInUser().getCompany();
        return mapperUtil.convert(company, new CompanyDto());


    }

    protected void displayInvoiceDetails(InvoiceDto invoiceDto) {
        BigDecimal totalPrice = getTotalPriceOfInvoice(invoiceDto);
        BigDecimal totalTax = getTotalTaxOfInvoice(invoiceDto);
        BigDecimal totalPriceWithTax = totalTax.add(totalTax);
        invoiceDto.setPrice(totalPrice);
        invoiceDto.setTax(totalTax);
        invoiceDto.setTotal(totalPriceWithTax);
    }

}

