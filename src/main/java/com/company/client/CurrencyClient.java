package com.company.client;

import com.company.annotation.ExecutionTime;
import com.company.dto.currency_api.CurrencyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(url = "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/usd.json", name = "CURRENCY-CLIENT")
public interface CurrencyClient {

    @GetMapping
    @ExecutionTime
    CurrencyResponse getAllCurrencies();

}