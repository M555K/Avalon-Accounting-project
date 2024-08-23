package com.company.service;

import com.company.dto.InvoiceDto;
import com.company.dto.currency_api.CurrencyRates;
import com.company.enums.InvoiceStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface DashboardService {
    List<InvoiceDto> getLastThreeApprovedInvoices(InvoiceStatus invoiceStatus);

    CurrencyRates getExchangeRates();

    Map<String, BigDecimal> getSummaryNumbers();

}
