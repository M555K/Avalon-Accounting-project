package com.company.service.impl;

import com.company.client.CurrencyClient;
import com.company.dto.InvoiceDto;
import com.company.dto.InvoiceProductDto;
import com.company.dto.currency_api.CurrencyRates;
import com.company.enums.InvoiceStatus;
import com.company.enums.InvoiceType;
import com.company.service.DashboardService;
import com.company.service.InvoiceProductService;
import com.company.service.InvoiceService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final InvoiceService invoiceService;
    private final CurrencyClient currencyClient;
    private final InvoiceProductService invoiceProductService;

    public DashboardServiceImpl(InvoiceService invoiceService, CurrencyClient currencyClient, InvoiceProductService invoiceProductService) {
        this.invoiceService = invoiceService;
        this.currencyClient = currencyClient;
        this.invoiceProductService = invoiceProductService;
    }

    @Override
    public List<InvoiceDto> getLastThreeApprovedInvoices(InvoiceStatus invoiceStatus) {
        return invoiceService.showLastThreeApprovedInvoices(invoiceStatus);
    }

    @Override
    public CurrencyRates getExchangeRates() {
        return currencyClient.getAllCurrencies().getCurrencyRates();
    }

    @Override
    public Map<String, BigDecimal> getSummaryNumbers() {

        Map<String, BigDecimal> summaryNumbers = new HashMap<>();

        BigDecimal totalCost = calculateAllApprovedInvoicesTotal(InvoiceType.PURCHASE);
        BigDecimal totalSales = calculateAllApprovedInvoicesTotal(InvoiceType.SALES);
        BigDecimal profitLoss = calculateTotalProfitLoss();

        summaryNumbers.put("totalCost", totalCost);
        summaryNumbers.put("totalSales", totalSales);
        summaryNumbers.put("profitLoss", profitLoss);

        return summaryNumbers;
    }

    private BigDecimal calculateTotalProfitLoss() {
        return invoiceService.listAllInvoicesByType(InvoiceType.SALES).stream()
                .filter(p->p.getInvoiceStatus() == InvoiceStatus.APPROVED)
                .map(p-> invoiceProductService.listAllByInvoiceId(p.getId()).stream()
                        .map(InvoiceProductDto::getProfitLoss)
                        .reduce(BigDecimal.ZERO,BigDecimal::add))
                .reduce(BigDecimal.ZERO,BigDecimal::add);
    }

    private BigDecimal calculateAllApprovedInvoicesTotal(InvoiceType invoiceType) {
        return invoiceService.listAllInvoicesByType(invoiceType).stream()
                .filter(p->p.getInvoiceStatus()== InvoiceStatus.APPROVED)
                .map(InvoiceDto::getTotal)
                .reduce(BigDecimal.ZERO,BigDecimal::add);
    }
}
