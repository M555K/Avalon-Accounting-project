package com.company.converter;

import com.company.dto.InvoiceProductDto;
import com.company.service.InvoiceProductService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class InvoiceProductDTOConverter implements Converter <String, InvoiceProductDto> {

    private final InvoiceProductService invoiceProductService;

    public InvoiceProductDTOConverter(InvoiceProductService invoiceProductService) {
        this.invoiceProductService = invoiceProductService;
    }

 //  @Override
 //  public InvoiceProductDto convert(Long source) {
 //      return invoiceProductService.findInvoiceProductById(source);
 //  }

    @Override
    public InvoiceProductDto convert(String source) {
        if (source==null || source.isEmpty()) {
            return null;
        }
        return invoiceProductService.findInvoiceProductById(Long.parseLong(source));
    }
}
