package com.company.service;

import com.company.entity.InvoiceProduct;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ReportingService {

    Map<String, BigDecimal> getMonthlyProfitLoss();

    List<InvoiceProduct> getChangesOfStock();

}
